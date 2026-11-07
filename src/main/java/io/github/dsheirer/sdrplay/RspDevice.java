package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import io.github.dsheirer.sdrplay.internal.Callbacks;
import io.github.dsheirer.sdrplay.internal.EventType;
import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.Native;
import io.github.dsheirer.sdrplay.internal.OverloadType;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * A selected SDRplay receiver. The subclass matches the model and adds its
 * model-specific controls. Close it when finished, which also stops streaming
 * and makes the device available to other applications.
 *
 * <p>Settings are stored in the API's parameter structures. Before
 * {@link #start} they take effect when streaming starts; while streaming,
 * each setter also sends the change to the device with
 * {@code sdrplay_api_Update}. Gain, frequency and sample rate changes complete
 * asynchronously, and the API rejects a second change of the same kind while
 * the first is in progress, so a setter waits up to 250 ms for the previous
 * change of its kind to take effect before sending the next.
 *
 * <p>Tuner settings, such as frequency and gain, are on the {@link RxChannel}
 * from {@link #channel()}. Device settings, such as sample rate, are here.
 *
 * <p>Methods may be called from any thread, including from a listener, except
 * {@link #stop()} and {@link #close()}. Do not close a device while other
 * threads are still using it.
 */
public abstract sealed class RspDevice implements AutoCloseable
        permits Rsp1Device, Rsp1aDevice, Rsp2Device, RspDuoDevice, RspDxDevice {

    private static final long UPDATE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(250);
    static final int PENDING_GAIN = 1;
    static final int PENDING_FREQUENCY = 2;
    static final int PENDING_SAMPLE_RATE = 4;

    /** What {@link SDRplay} hands to a new device after selecting it. */
    record Selection(SDRplay session, Native api, DeviceInfo info, Arena arena, MemorySegment device,
            MemorySegment deviceParams, Tuner tuner, RspDuoMode rspDuoMode) {
    }

    final Native api;
    private final SDRplay session;
    private final DeviceInfo info;
    private final Arena arena;
    private final MemorySegment device;
    private final MemorySegment handle;
    private final Tuner tuner;
    private final RspDuoMode rspDuoMode;
    private final MemorySegment context;
    private final MemorySegment deviceParams;

    private final RxChannel channelA;
    private final RxChannel channelB;
    private final RxChannel primary;
    private final AtomicInteger[] pending = {new AtomicInteger(), new AtomicInteger()};

    private final Object lifecycleLock = new Object();
    private final Object updateLock = new Object();
    private volatile boolean closed;
    private volatile boolean streaming;
    private volatile SampleListener sampleListener;
    private volatile DeviceEventListener eventListener;
    private volatile Throwable listenerFailure;
    private volatile Thread streamAThread;
    private volatile Thread streamBThread;
    private volatile Thread eventThread;

    RspDevice(Selection selection) {
        this.session = selection.session();
        this.api = selection.api();
        this.info = selection.info();
        this.arena = selection.arena();
        this.device = selection.device();
        this.deviceParams = selection.deviceParams();
        this.tuner = selection.tuner();
        this.rspDuoMode = selection.rspDuoMode();
        this.handle = device.get(ADDRESS, Layouts.offset(Layouts.DEVICE, "dev"));
        // Tuner B uses rxChannelB, also when it is the only tuner selected.
        this.channelA = tuner == Tuner.B ? null : new RxChannel(this, Tuner.A, 0);
        this.channelB = tuner == Tuner.B || tuner == Tuner.BOTH ? new RxChannel(this, Tuner.B, 1) : null;
        this.primary = channelA != null ? channelA : channelB;
        this.context = Callbacks.register(new CallbackTarget());
    }

    // ---- Information ----

    /** Returns the device as {@link SDRplay#devices()} reported it when it was selected. */
    public DeviceInfo info() {
        return info;
    }

    /** Returns the receiver model. */
    public RspModel model() {
        return info.model();
    }

    /** Returns the serial number. */
    public String serialNumber() {
        return info.serialNumber();
    }

    /** Returns the selected tuner: {@link Tuner#A}, except on an RSPduo. */
    public Tuner tuner() {
        return tuner;
    }

    /**
     * Returns the tuner settings of the selected tuner. On an RSPduo in dual
     * tuner mode this is tuner A; see {@link RspDuoDevice#channel(Tuner)}.
     */
    public RxChannel channel() {
        return primary;
    }

    /** Returns the tuner settings of every selected tuner: two for an RSPduo in dual tuner mode, otherwise one. */
    public List<RxChannel> channels() {
        List<RxChannel> channels = new ArrayList<>(2);
        if (channelA != null) {
            channels.add(channelA);
        }
        if (channelB != null) {
            channels.add(channelB);
        }
        return List.copyOf(channels);
    }

    /** Returns the extended description of the last error the API recorded for this device, or null. */
    public ErrorInfo lastError() {
        checkOpen();
        MemorySegment pointer = Native.invokePointer(api.getLastError, device);
        if (pointer.equals(MemorySegment.NULL)) {
            return null;
        }
        MemorySegment error = pointer.reinterpret(Layouts.ERROR_INFO.byteSize());
        return new ErrorInfo(
            Layouts.string(error, Layouts.offset(Layouts.ERROR_INFO, "file"), 256),
            Layouts.string(error, Layouts.offset(Layouts.ERROR_INFO, "function"), 256),
            error.get(JAVA_INT, Layouts.offset(Layouts.ERROR_INFO, "line")),
            Layouts.string(error, Layouts.offset(Layouts.ERROR_INFO, "message"), 1024));
    }

    /** Sets how much the API writes to its debug log for this device. */
    public void setDebugLevel(DebugLevel level) throws SDRplayException {
        check("sdrplay_api_DebugEnable", Native.invoke(api.debugEnable, handle(), level.code()));
    }

    // ---- Device settings ----

    /** Returns the ADC sample rate in Hz, before decimation. */
    public double sampleRate() {
        return devParams().get(JAVA_DOUBLE, DevParams.FS_HZ);
    }

    /**
     * Sets the ADC sample rate in Hz, before decimation. The API accepts 2 MHz
     * to 10.66 MHz, or 62.5 kHz to 2 MHz on some models with low IF. On an
     * RSPduo in dual tuner, master or slave mode the rate is fixed.
     */
    public void setSampleRate(double hertz) throws SDRplayException {
        requirePositive(hertz, "Sample rate");
        updateDevice(UpdateReason.DEV_FS, () -> devParams().set(JAVA_DOUBLE, DevParams.FS_HZ, hertz));
    }

    /** Returns the frequency correction in parts per million. */
    public double ppm() {
        return devParams().get(JAVA_DOUBLE, DevParams.PPM);
    }

    /** Sets the frequency correction in parts per million. */
    public void setPpm(double ppm) throws SDRplayException {
        if (!Double.isFinite(ppm)) {
            throw new IllegalArgumentException("PPM must be finite: " + ppm);
        }
        updateDevice(UpdateReason.DEV_PPM, () -> devParams().set(JAVA_DOUBLE, DevParams.PPM, ppm));
    }

    /** Returns the USB transfer mode. */
    public TransferMode transferMode() {
        return TransferMode.fromCode(devParams().get(JAVA_INT, DevParams.MODE));
    }

    /**
     * Sets the USB transfer mode, which the API reads only when streaming
     * starts.
     *
     * @throws IllegalStateException if the device is streaming
     */
    public void setTransferMode(TransferMode mode) {
        Objects.requireNonNull(mode, "mode");
        synchronized (updateLock) {
            if (streaming) {
                throw new IllegalStateException("The transfer mode cannot change while streaming");
            }
            devParams().set(JAVA_INT, DevParams.MODE, mode.code());
        }
    }

    /**
     * Returns {@code samplesPerPkt}, which the API documents as the number of
     * samples per stream callback. API 3.15 leaves it at 0, at least on Linux;
     * use {@link SampleBuffer#sampleCount()} instead.
     */
    public int samplesPerPacket() {
        return devParams().get(JAVA_INT, DevParams.SAMPLES_PER_PKT);
    }

    // ---- Streaming ----

    /**
     * Starts streaming with {@code sdrplay_api_Init}.
     *
     * @param samples receives the samples
     * @param events receives device events, or null to ignore them
     * @throws IllegalStateException if the device is already streaming
     */
    public void start(SampleListener samples, DeviceEventListener events) throws SDRplayException {
        Objects.requireNonNull(samples, "samples");
        synchronized (lifecycleLock) {
            MemorySegment dev = handle();
            if (streaming) {
                throw new IllegalStateException("Device is already streaming");
            }
            listenerFailure = null;
            sampleListener = samples;
            eventListener = events;
            pending[0].set(0);
            pending[1].set(0);
            int result = Native.invoke(api.init, dev, api.callbackFunctions, context);
            if (result != 0) {
                sampleListener = null;
                eventListener = null;
                throw exception("sdrplay_api_Init", result);
            }
            streaming = true;
        }
    }

    /** Starts streaming without an event listener. See {@link #start(SampleListener, DeviceEventListener)}. */
    public void start(SampleListener samples) throws SDRplayException {
        start(samples, null);
    }

    /**
     * Stops streaming with {@code sdrplay_api_Uninit}, which waits for the
     * listeners to return. Does nothing if the device is not streaming.
     *
     * @throws IllegalStateException if called from a listener
     */
    public void stop() throws SDRplayException {
        rejectCallbackThread("stop");
        synchronized (lifecycleLock) {
            synchronized (updateLock) {
                if (!streaming) {
                    return;
                }
                try {
                    check("sdrplay_api_Uninit", Native.invoke(api.uninit, handle()));
                } finally {
                    streaming = false;
                    sampleListener = null;
                    eventListener = null;
                    streamAThread = null;
                    streamBThread = null;
                    eventThread = null;
                }
            }
        }
    }

    /** Returns true between {@link #start} and {@link #stop()}. */
    public boolean isStreaming() {
        return streaming;
    }

    /** Returns the exception that a listener threw, which stopped delivery, or null if there was none. */
    public Throwable listenerFailure() {
        return listenerFailure;
    }

    // ---- Lifecycle ----

    /**
     * Stops streaming and releases the device with
     * {@code sdrplay_api_ReleaseDevice}. Closing again does nothing.
     *
     * @throws IllegalStateException if called from a listener
     */
    @Override
    public void close() throws SDRplayException {
        rejectCallbackThread("close");
        synchronized (lifecycleLock) {
            if (closed) {
                return;
            }
            try {
                stop();
            } finally {
                closed = true;
                Callbacks.unregister(context);
                session.forget(this);
                try {
                    check("sdrplay_api_ReleaseDevice", Native.invoke(api.releaseDevice, device));
                } finally {
                    arena.close();
                }
            }
        }
    }

    /** Returns true once the device has been closed. */
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        String text = info.toString();
        return tuner == Tuner.A && rspDuoMode == RspDuoMode.UNKNOWN ? text : text + " " + rspDuoMode + " " + tuner;
    }

    // ---- For subclasses and RxChannel ----

    /** Returns the RSPduo mode the device was selected in, or {@link RspDuoMode#UNKNOWN} for other models. */
    RspDuoMode rspDuoMode() {
        return rspDuoMode;
    }

    /** Returns the channel of a tuner, or null if that tuner is not selected. */
    RxChannel channelOrNull(Tuner which) {
        return switch (which) {
            case A -> channelA;
            case B -> channelB;
            default -> null;
        };
    }

    MemorySegment device() {
        return device;
    }

    MemorySegment handle() {
        checkOpen();
        return handle;
    }

    void checkOpen() {
        if (closed) {
            throw new IllegalStateException("Device is closed");
        }
    }

    /**
     * Returns {@code sdrplay_api_DevParamsT}.
     *
     * @throws IllegalStateException for an RSPduo in slave mode, whose device settings belong to the master
     */
    MemorySegment devParams() {
        checkOpen();
        MemorySegment pointer = deviceParams.get(ADDRESS, Layouts.offset(Layouts.DEVICE_PARAMS, "devParams"));
        if (pointer.equals(MemorySegment.NULL)) {
            throw new IllegalStateException("Device settings are controlled by the RSPduo master, not this slave");
        }
        return pointer.reinterpret(Layouts.DEV_PARAMS.byteSize());
    }

    /** Returns the {@code sdrplay_api_RxChannelParamsT} of channel 0 (A) or 1 (B). */
    MemorySegment rxChannelParams(int index) {
        checkOpen();
        String field = index == 0 ? "rxChannelA" : "rxChannelB";
        MemorySegment pointer = deviceParams.get(ADDRESS, Layouts.offset(Layouts.DEVICE_PARAMS, field));
        if (pointer.equals(MemorySegment.NULL)) {
            throw new IllegalStateException("The API has no parameters for tuner " + (index == 0 ? "A" : "B"));
        }
        return pointer.reinterpret(Layouts.RX_CHANNEL_PARAMS.byteSize());
    }

    /** Applies a change to the device settings, then sends it to the device if streaming. */
    void updateDevice(UpdateReason reason, Runnable write) throws SDRplayException {
        update(tuner, primary.index(), reason, write);
    }

    /** Applies a change to a tuner's settings, then sends it to the device if streaming. */
    void updateChannel(RxChannel channel, UpdateReason reason, Runnable write) throws SDRplayException {
        update(channel.tuner(), channel.index(), reason, write);
    }

    private void update(Tuner target, int index, UpdateReason reason, Runnable write) throws SDRplayException {
        synchronized (updateLock) {
            MemorySegment dev = handle();
            write.run();
            if (!streaming) {
                return;
            }
            int bits = switch (reason) {
                case TUNER_GR -> PENDING_GAIN;
                case TUNER_FRF -> PENDING_FREQUENCY;
                case DEV_FS -> PENDING_SAMPLE_RATE;
                default -> 0;
            };
            if (bits != 0) {
                // A stream callback clears the bit, so a listener must not wait for it.
                if (!isCallbackThread()) {
                    awaitPending(index, bits);
                }
                pending[index].getAndUpdate(value -> value | bits);
            }
            int result = Native.invoke(api.update, dev, target.code(), reason.reason(), reason.reasonExtension1());
            if (result != 0) {
                pending[index].getAndUpdate(value -> value & ~bits);
                throw exception("sdrplay_api_Update", result);
            }
        }
    }

    private void awaitPending(int index, int bits) {
        long deadline = System.nanoTime() + UPDATE_TIMEOUT_NANOS;
        while ((pending[index].get() & bits) != 0 && System.nanoTime() - deadline < 0) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        }
        // On timeout, try anyway: the API reports a real conflict as an error.
        pending[index].getAndUpdate(value -> value & ~bits);
    }

    void check(String function, int result) throws SDRplayException {
        if (result != 0) {
            throw exception(function, result);
        }
    }

    SDRplayException exception(String function, int result) {
        String detail = api.errorString(result);
        if (!closed) {
            ErrorInfo last = lastError();
            if (last != null && !last.message().isBlank()) {
                detail = detail == null ? last.message() : detail + "; " + last.message();
            }
        }
        return new SDRplayException(function, result, detail);
    }

    private boolean isCallbackThread() {
        Thread current = Thread.currentThread();
        return current == streamAThread || current == streamBThread || current == eventThread;
    }

    private void rejectCallbackThread(String method) {
        if (isCallbackThread()) {
            throw new IllegalStateException(method + " cannot be called from a listener");
        }
    }

    static void requirePositive(double value, String what) {
        if (!(value > 0) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(what + " must be positive: " + value);
        }
    }

    /** Offsets in {@code sdrplay_api_DevParamsT}. */
    static final class DevParams {
        static final long PPM = Layouts.offset(Layouts.DEV_PARAMS, "ppm");
        static final long FS_HZ = Layouts.offset(Layouts.DEV_PARAMS, "fsFreq", "fsHz");
        static final long MODE = Layouts.offset(Layouts.DEV_PARAMS, "mode");
        static final long SAMPLES_PER_PKT = Layouts.offset(Layouts.DEV_PARAMS, "samplesPerPkt");

        private DevParams() {
        }
    }

    /** Receives the API's callbacks for this device. Nothing may escape from here. */
    private final class CallbackTarget implements Callbacks.Target {
        private static final long FIRST_SAMPLE = Layouts.offset(Layouts.STREAM_CB_PARAMS, "firstSampleNum");
        private static final long GR_CHANGED = Layouts.offset(Layouts.STREAM_CB_PARAMS, "grChanged");
        private static final long RF_CHANGED = Layouts.offset(Layouts.STREAM_CB_PARAMS, "rfChanged");
        private static final long FS_CHANGED = Layouts.offset(Layouts.STREAM_CB_PARAMS, "fsChanged");

        @Override
        public void onStream(boolean streamB, MemorySegment xi, MemorySegment xq, MemorySegment params,
                int numSamples, int reset) {
            RxChannel channel = streamB && channelB != null ? channelB : primary;
            if (streamB) {
                streamBThread = Thread.currentThread();
            } else {
                streamAThread = Thread.currentThread();
            }

            boolean gainChanged = false;
            boolean frequencyChanged = false;
            boolean sampleRateChanged = false;
            long firstSample = 0;
            if (!params.equals(MemorySegment.NULL)) {
                MemorySegment p = params.reinterpret(Layouts.STREAM_CB_PARAMS.byteSize());
                firstSample = Integer.toUnsignedLong(p.get(JAVA_INT, FIRST_SAMPLE));
                gainChanged = p.get(JAVA_INT, GR_CHANGED) != 0;
                frequencyChanged = p.get(JAVA_INT, RF_CHANGED) != 0;
                sampleRateChanged = p.get(JAVA_INT, FS_CHANGED) != 0;
            }
            int cleared = (gainChanged ? PENDING_GAIN : 0) | (frequencyChanged ? PENDING_FREQUENCY : 0);
            if (cleared != 0) {
                pending[channel.index()].getAndUpdate(value -> value & ~cleared);
            }
            if (sampleRateChanged) {
                pending[0].getAndUpdate(value -> value & ~PENDING_SAMPLE_RATE);
                pending[1].getAndUpdate(value -> value & ~PENDING_SAMPLE_RATE);
            }

            SampleListener listener = sampleListener;
            if (listener == null || listenerFailure != null || numSamples <= 0) {
                return;
            }
            // Closing this arena ends the segments' lifetime, so a listener that
            // keeps them gets an exception and not freed native memory.
            try (Arena callbackArena = Arena.ofConfined()) {
                long bytes = numSamples * 2L;
                MemorySegment i = xi.reinterpret(bytes, callbackArena, null);
                MemorySegment q = xq.reinterpret(bytes, callbackArena, null);
                listener.onSamples(new SampleBuffer(channel.tuner(), i, q, numSamples, firstSample, reset != 0,
                    gainChanged, frequencyChanged, sampleRateChanged));
            } catch (Throwable failure) {
                recordFailure(failure);
            }
        }

        @Override
        public void onEvent(int eventId, int tunerCode, MemorySegment params) {
            eventThread = Thread.currentThread();
            DeviceEvent event;
            try {
                event = toEvent(eventId, tunerCode, params);
            } catch (RuntimeException e) {
                // An event this version of jsdrplay does not know, or unreadable parameters.
                return;
            }
            DeviceEventListener listener = eventListener;
            if (event == null || listener == null || listenerFailure != null) {
                return;
            }
            try {
                listener.onEvent(event);
            } catch (Throwable failure) {
                recordFailure(failure);
            }
        }

        private DeviceEvent toEvent(int eventId, int tunerCode, MemorySegment params) {
            Tuner eventTuner = Tuner.fromCode(tunerCode);
            MemorySegment p = params.equals(MemorySegment.NULL)
                ? MemorySegment.NULL : params.reinterpret(Layouts.EVENT_PARAMS.byteSize());
            return switch (EventType.fromCode(eventId)) {
                case GAIN_CHANGE -> new DeviceEvent.GainChanged(eventTuner,
                    p.get(JAVA_INT, Events.GAIN_GR), p.get(JAVA_INT, Events.GAIN_LNA_GR),
                    p.get(JAVA_DOUBLE, Events.GAIN_CURRENT));
                case POWER_OVERLOAD_CHANGE -> {
                    // The API requires an acknowledgement before it reports the next change.
                    Native.invoke(api.update, handle, tunerCode, UpdateReason.CTRL_OVERLOAD_MSG_ACK.reason(), 0);
                    yield new DeviceEvent.PowerOverload(eventTuner,
                        p.get(JAVA_INT, Events.OVERLOAD) == OverloadType.DETECTED.code());
                }
                case RSPDUO_MODE_CHANGE -> new DeviceEvent.RspDuoModeChanged(eventTuner,
                    RspDuoModeChange.fromCode(p.get(JAVA_INT, Events.RSPDUO_MODE)));
                case DEVICE_REMOVED -> new DeviceEvent.DeviceRemoved(eventTuner);
                case DEVICE_FAILURE -> new DeviceEvent.DeviceFailure(eventTuner);
            };
        }

        private void recordFailure(Throwable failure) {
            if (listenerFailure == null) {
                listenerFailure = failure;
            }
        }
    }

    /** Offsets in {@code sdrplay_api_EventParamsT}. */
    private static final class Events {
        static final long GAIN_GR = Layouts.offset(Layouts.EVENT_PARAMS, "gainParams", "gRdB");
        static final long GAIN_LNA_GR = Layouts.offset(Layouts.EVENT_PARAMS, "gainParams", "lnaGRdB");
        static final long GAIN_CURRENT = Layouts.offset(Layouts.EVENT_PARAMS, "gainParams", "currGain");
        static final long OVERLOAD =
            Layouts.offset(Layouts.EVENT_PARAMS, "powerOverloadParams", "powerOverloadChangeType");
        static final long RSPDUO_MODE = Layouts.offset(Layouts.EVENT_PARAMS, "rspDuoModeParams", "modeChangeType");

        private Events() {
        }
    }
}
