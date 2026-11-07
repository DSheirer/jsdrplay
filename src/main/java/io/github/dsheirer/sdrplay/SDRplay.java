package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.Native;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Entry point of the library: a session with the SDRplay API, which finds and
 * selects devices.
 *
 * <pre>{@code
 * try (SDRplay sdrplay = SDRplay.open();
 *      RspDevice device = sdrplay.select(sdrplay.devices().getFirst())) {
 *     device.setSampleRate(8_000_000);
 *     device.channel().setDecimation(4, false);
 *     device.channel().setFrequency(100_000_000);
 *     device.start(buffer -> process(buffer.copyInterleavedFloats()));
 *     Thread.sleep(1000);
 * }
 * }</pre>
 *
 * <p>The SDRplay API is not part of jsdrplay: install it from
 * <a href="https://www.sdrplay.com/api/">sdrplay.com</a>. It consists of a
 * library, which jsdrplay loads, and a service, which owns the devices.
 *
 * <p>Sessions share one connection to the API: the first {@link #open()} calls
 * {@code sdrplay_api_Open} and the last {@link #close()} calls
 * {@code sdrplay_api_Close}. The methods of all sessions run one at a time.
 *
 * <p>The API allows one selected device per API client, and an RSPduo slave
 * must use a different client from its master. So when this application
 * already has a device selected with the current client, selecting another
 * device, or the slave of its own master, first calls {@code sdrplay_api_Open}
 * again, which starts a new client. The API does not count these calls, and a
 * second {@code sdrplay_api_Close} would free its resources twice, so the last
 * {@link #close()} still calls {@code sdrplay_api_Close} only once.
 */
public final class SDRplay implements AutoCloseable {
    /** The API version these bindings were written and checked against. */
    public static final float API_VERSION = 3.15f;

    private static final Object LOCK = new Object();
    private static int openSessions;
    private static float openVersion;
    /** Incremented by each {@code sdrplay_api_Open}, which starts a new API client. Guarded by LOCK. */
    private static int client;
    /** Devices selected by this process and the client that selected each one. Guarded by LOCK. */
    private static final Map<RspDevice, Integer> SELECTED = new IdentityHashMap<>();

    private final Native api;
    private final float version;
    private final List<RspDevice> devices = new CopyOnWriteArrayList<>();
    private boolean closed;

    private SDRplay(Native api, float version) {
        this.api = api;
        this.version = version;
    }

    /** Returns true if the SDRplay API library is installed and can be loaded. */
    public static boolean isInstalled() {
        try {
            Native.get();
            return true;
        } catch (UnsatisfiedLinkError e) {
            return false;
        }
    }

    /**
     * Returns where the SDRplay API library was loaded from.
     *
     * @throws SDRplayException with {@link SDRplayError#API_NOT_FOUND} if it is not installed
     */
    public static String libraryLocation() throws SDRplayException {
        return load().location;
    }

    /**
     * Opens a session, connecting to the SDRplay API service if no other
     * session is open.
     *
     * @throws SDRplayException with {@link SDRplayError#API_NOT_FOUND} if the API is not installed,
     *     {@link SDRplayError#UNSUPPORTED_API_VERSION} if it is older than 3.15, or an API error,
     *     typically {@link SDRplayError#SERVICE_NOT_RESPONDING}, if the service is not running
     */
    public static SDRplay open() throws SDRplayException {
        Native api = load();
        synchronized (LOCK) {
            if (openSessions == 0) {
                check("sdrplay_api_Open", Native.invoke(api.open), api);
                client++;
                try {
                    openVersion = readVersion(api);
                    checkVersion(openVersion);
                } catch (SDRplayException | RuntimeException e) {
                    Native.invoke(api.close);
                    throw e;
                }
            }
            openSessions++;
            return new SDRplay(api, openVersion);
        }
    }

    private static Native load() throws SDRplayException {
        try {
            return Native.get();
        } catch (UnsatisfiedLinkError e) {
            throw new SDRplayException(SDRplayError.API_NOT_FOUND, e.getMessage(), e);
        }
    }

    private static float readVersion(Native api) throws SDRplayException {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment version = arena.allocate(JAVA_FLOAT);
            check("sdrplay_api_ApiVersion", Native.invoke(api.apiVersion, version), api);
            return version.get(JAVA_FLOAT, 0);
        }
    }

    /**
     * Accepts API 3.15 and later 3.x versions. Earlier versions lay out the
     * parameter structures differently, so using them with these bindings
     * would corrupt memory.
     */
    static void checkVersion(float version) throws SDRplayException {
        if (!isSupportedVersion(version)) {
            throw new SDRplayException(SDRplayError.UNSUPPORTED_API_VERSION, "The installed SDRplay API is version "
                + version + "; jsdrplay needs version " + API_VERSION + " or a later 3.x version. Install the "
                + "current API from https://www.sdrplay.com/api/", null);
        }
    }

    /** Returns true for API versions these bindings can use: 3.15 and later 3.x. */
    public static boolean isSupportedVersion(float version) {
        // The API reports a float such as 3.15f; compare hundredths to avoid rounding surprises.
        int hundredths = Math.round(version * 100);
        return hundredths >= Math.round(API_VERSION * 100) && hundredths < 400;
    }

    /** Returns the version of the installed SDRplay API, such as 3.15. */
    public float apiVersion() {
        return version;
    }

    /** Returns where the SDRplay API library was loaded from. */
    public String location() {
        return api.location;
    }

    /**
     * Lists the devices the API reports. A device another application is
     * using is not listed, except an RSPduo whose other tuner is free.
     */
    public List<DeviceInfo> devices() throws SDRplayException {
        synchronized (LOCK) {
            checkOpen();
            lock();
            try (Arena arena = Arena.ofConfined()) {
                List<DeviceInfo> result = new ArrayList<>();
                MemorySegment list = getDevices(arena);
                int count = (int) (list.byteSize() / Layouts.DEVICE.byteSize());
                for (int n = 0; n < count; n++) {
                    result.add(describe(entry(list, n)));
                }
                return List.copyOf(result);
            } finally {
                unlock();
            }
        }
    }

    /**
     * Selects a device for exclusive use. An RSPduo is selected in single
     * tuner mode on tuner A, or B if only B is free, or as slave if another
     * application is its master; use {@link #selectRspDuo} for other modes.
     *
     * @throws SDRplayException if the device is gone or already in use
     */
    public RspDevice select(DeviceInfo device) throws SDRplayException {
        Objects.requireNonNull(device, "device");
        if (device.model() != RspModel.RSPDUO) {
            return select(device.serialNumber(), RspDuoMode.UNKNOWN, Tuner.A, 0);
        }
        if (device.rspDuoModes().contains(RspDuoMode.SINGLE_TUNER)) {
            return select(device.serialNumber(), RspDuoMode.SINGLE_TUNER,
                device.tuners().contains(Tuner.A) ? Tuner.A : Tuner.B, 0);
        }
        if (device.rspDuoModes().contains(RspDuoMode.SLAVE)) {
            return select(device.serialNumber(), RspDuoMode.SLAVE,
                device.tuners().contains(Tuner.B) ? Tuner.B : Tuner.A, 0);
        }
        throw new IllegalArgumentException("The RSPduo offers no mode to select: " + device.rspDuoModes());
    }

    /**
     * Selects the device with a serial number. See {@link #select(DeviceInfo)}.
     *
     * @throws IllegalArgumentException if no listed device has that serial number
     */
    public RspDevice select(String serialNumber) throws SDRplayException {
        Objects.requireNonNull(serialNumber, "serialNumber");
        for (DeviceInfo device : devices()) {
            if (device.serialNumber().equals(serialNumber)) {
                return select(device);
            }
        }
        throw new IllegalArgumentException("No SDRplay device with serial number " + serialNumber);
    }

    /**
     * Selects an RSPduo in a particular mode.
     *
     * <p>This application can select both the master and the slave, each with
     * its own API client; see {@link SDRplay}. Select the master first;
     * {@link #devices()} lists the slave mode only after that.
     *
     * @param device an RSPduo from {@link #devices()}
     * @param mode one of the modes the device offers
     * @param tuner {@link Tuner#BOTH} for {@link RspDuoMode#DUAL_TUNER}; otherwise A or B
     * @param masterSampleRate for {@link RspDuoMode#MASTER}, the sample rate of both tuners: 6 or 8 MHz;
     *     ignored in the other modes
     */
    public RspDuoDevice selectRspDuo(DeviceInfo device, RspDuoMode mode, Tuner tuner, double masterSampleRate)
            throws SDRplayException {
        Objects.requireNonNull(device, "device");
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(tuner, "tuner");
        if (device.model() != RspModel.RSPDUO) {
            throw new IllegalArgumentException("Not an RSPduo: " + device);
        }
        if (!device.rspDuoModes().contains(mode)) {
            throw new IllegalArgumentException("The RSPduo offers " + device.rspDuoModes() + ", not " + mode);
        }
        boolean dual = mode == RspDuoMode.DUAL_TUNER;
        if (dual != (tuner == Tuner.BOTH) || tuner == Tuner.NEITHER) {
            throw new IllegalArgumentException(mode + " cannot use tuner " + tuner);
        }
        if (!dual && !device.tuners().contains(tuner)) {
            throw new IllegalArgumentException("Tuner " + tuner + " is in use; free: " + device.tuners());
        }
        if (mode == RspDuoMode.MASTER && masterSampleRate != 6_000_000 && masterSampleRate != 8_000_000) {
            throw new IllegalArgumentException("Master sample rate must be 6 or 8 MHz: " + masterSampleRate);
        }
        return (RspDuoDevice) select(device.serialNumber(), mode, tuner, masterSampleRate);
    }

    /**
     * Stops the API from checking that this application is still alive, so a
     * debugger can pause it without the service releasing the device. Must be
     * called before selecting a device. For debugging only.
     */
    public void disableHeartbeat() throws SDRplayException {
        synchronized (LOCK) {
            checkOpen();
            check("sdrplay_api_DisableHeartbeat", Native.invoke(api.disableHeartbeat), api);
        }
    }

    /**
     * Closes the devices selected through this session, then the session. The
     * last session to close disconnects from the API service. Closing again
     * does nothing.
     */
    @Override
    public void close() throws SDRplayException {
        SDRplayException failure = null;
        for (RspDevice device : devices) {
            try {
                device.close();
            } catch (SDRplayException e) {
                failure = failure == null ? e : failure;
            }
        }
        synchronized (LOCK) {
            if (!closed) {
                closed = true;
                if (--openSessions == 0) {
                    int result = Native.invoke(api.close);
                    if (result != 0 && failure == null) {
                        failure = new SDRplayException("sdrplay_api_Close", result, api.errorString(result));
                    }
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }

    // ---- Selection ----

    private RspDevice select(String serialNumber, RspDuoMode mode, Tuner tuner, double masterSampleRate)
            throws SDRplayException {
        synchronized (LOCK) {
            checkOpen();
            if (SELECTED.containsValue(client)) {
                check("sdrplay_api_Open", Native.invoke(api.open), api);
                client++;
            }
            Arena deviceArena = Arena.ofShared();
            try {
                MemorySegment selected = deviceArena.allocate(Layouts.DEVICE);
                DeviceInfo info;
                lock();
                try (Arena arena = Arena.ofConfined()) {
                    MemorySegment list = getDevices(arena);
                    MemorySegment entry = find(list, serialNumber);
                    info = describe(entry);
                    selected.copyFrom(entry);
                    if (info.model() == RspModel.RSPDUO) {
                        selected.set(JAVA_INT, Offsets.TUNER, tuner.code());
                        selected.set(JAVA_INT, Offsets.RSPDUO_MODE, mode.code());
                        if (mode == RspDuoMode.MASTER) {
                            selected.set(JAVA_DOUBLE, Offsets.RSPDUO_SAMPLE_FREQ, masterSampleRate);
                        }
                    }
                    check("sdrplay_api_SelectDevice", Native.invoke(api.selectDevice, selected), api);
                } finally {
                    unlock();
                }

                MemorySegment params;
                try (Arena arena = Arena.ofConfined()) {
                    MemorySegment pointer = arena.allocate(ADDRESS);
                    MemorySegment handle = selected.get(ADDRESS, Offsets.DEV);
                    int result = Native.invoke(api.getDeviceParams, handle, pointer);
                    if (result != 0) {
                        Native.invoke(api.releaseDevice, selected);
                        throw new SDRplayException("sdrplay_api_GetDeviceParams", result, api.errorString(result));
                    }
                    params = pointer.get(ADDRESS, 0).reinterpret(Layouts.DEVICE_PARAMS.byteSize());
                }

                Tuner selectedTuner = info.model() == RspModel.RSPDUO ? tuner : Tuner.A;
                RspDevice.Selection selection = new RspDevice.Selection(this, api, info, deviceArena, selected,
                    params, selectedTuner, info.model() == RspModel.RSPDUO ? mode : RspDuoMode.UNKNOWN);
                RspDevice device = switch (info.model()) {
                    case RSP1 -> new Rsp1Device(selection);
                    case RSP1A, RSP1B -> new Rsp1aDevice(selection);
                    case RSP2 -> new Rsp2Device(selection);
                    case RSPDUO -> new RspDuoDevice(selection);
                    case RSPDX, RSPDX_R2 -> new RspDxDevice(selection);
                    // A model newer than these bindings: its common controls still work.
                    case UNKNOWN -> new Rsp1Device(selection);
                };
                devices.add(device);
                SELECTED.put(device, client);
                return device;
            } catch (SDRplayException | RuntimeException e) {
                deviceArena.close();
                throw e;
            }
        }
    }

    /** Called by a device when it closes. */
    void forget(RspDevice device) {
        devices.remove(device);
        synchronized (LOCK) {
            SELECTED.remove(device);
        }
    }

    private MemorySegment getDevices(Arena arena) throws SDRplayException {
        MemorySegment list = arena.allocate(Layouts.DEVICE, Layouts.MAX_DEVICES);
        MemorySegment count = arena.allocate(JAVA_INT);
        check("sdrplay_api_GetDevices", Native.invoke(api.getDevices, list, count, Layouts.MAX_DEVICES), api);
        int found = Math.clamp(Integer.toUnsignedLong(count.get(JAVA_INT, 0)), 0, Layouts.MAX_DEVICES);
        return list.asSlice(0, found * Layouts.DEVICE.byteSize());
    }

    private static MemorySegment entry(MemorySegment list, int index) {
        return list.asSlice(index * Layouts.DEVICE.byteSize(), Layouts.DEVICE.byteSize());
    }

    private static MemorySegment find(MemorySegment list, String serialNumber) {
        int count = (int) (list.byteSize() / Layouts.DEVICE.byteSize());
        for (int n = 0; n < count; n++) {
            MemorySegment entry = entry(list, n);
            if (serial(entry).equals(serialNumber)) {
                return entry;
            }
        }
        throw new IllegalArgumentException("SDRplay device " + serialNumber + " is no longer available");
    }

    static DeviceInfo describe(MemorySegment entry) {
        int hardwareVersion = Byte.toUnsignedInt(entry.get(JAVA_BYTE, Offsets.HW_VER));
        RspModel model = RspModel.fromCode(hardwareVersion);
        Set<Tuner> tuners = EnumSet.of(Tuner.A);
        Set<RspDuoMode> modes = EnumSet.noneOf(RspDuoMode.class);
        double sampleRate = 0;
        if (model == RspModel.RSPDUO) {
            tuners = Tuner.fromMask(entry.get(JAVA_INT, Offsets.TUNER));
            modes = RspDuoMode.fromMask(entry.get(JAVA_INT, Offsets.RSPDUO_MODE));
            sampleRate = entry.get(JAVA_DOUBLE, Offsets.RSPDUO_SAMPLE_FREQ);
        }
        return new DeviceInfo(serial(entry), model, hardwareVersion, tuners, modes, sampleRate,
            entry.get(JAVA_BYTE, Offsets.VALID) != 0);
    }

    private static String serial(MemorySegment entry) {
        return Layouts.string(entry, Offsets.SER_NO, Layouts.MAX_SER_NO_LEN);
    }

    private void lock() throws SDRplayException {
        check("sdrplay_api_LockDeviceApi", Native.invoke(api.lockDeviceApi), api);
    }

    private void unlock() throws SDRplayException {
        check("sdrplay_api_UnlockDeviceApi", Native.invoke(api.unlockDeviceApi), api);
    }

    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException("SDRplay session is closed");
        }
    }

    private static void check(String function, int result, Native api) throws SDRplayException {
        if (result != 0) {
            throw new SDRplayException(function, result, api.errorString(result));
        }
    }

    /** Offsets in {@code sdrplay_api_DeviceT}. */
    private static final class Offsets {
        static final long SER_NO = Layouts.offset(Layouts.DEVICE, "SerNo");
        static final long HW_VER = Layouts.offset(Layouts.DEVICE, "hwVer");
        static final long TUNER = Layouts.offset(Layouts.DEVICE, "tuner");
        static final long RSPDUO_MODE = Layouts.offset(Layouts.DEVICE, "rspDuoMode");
        static final long VALID = Layouts.offset(Layouts.DEVICE, "valid");
        static final long RSPDUO_SAMPLE_FREQ = Layouts.offset(Layouts.DEVICE, "rspDuoSampleFreq");
        static final long DEV = Layouts.offset(Layouts.DEVICE, "dev");

        private Offsets() {
        }
    }
}
