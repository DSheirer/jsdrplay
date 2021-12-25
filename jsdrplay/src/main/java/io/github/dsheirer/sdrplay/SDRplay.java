package io.github.dsheirer.sdrplay;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_ErrorInfoT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;
import io.github.dsheirer.sdrplay.callback.CallbackFunctions;
import io.github.dsheirer.sdrplay.callback.IDeviceEventListener;
import io.github.dsheirer.sdrplay.callback.IStreamListener;
import io.github.dsheirer.sdrplay.device.Device;
import io.github.dsheirer.sdrplay.device.DeviceFactory;
import io.github.dsheirer.sdrplay.device.DeviceType;
import io.github.dsheirer.sdrplay.device.RspDuoDevice;
import io.github.dsheirer.sdrplay.device.RspDuoDualIndependentTunerDevice;
import io.github.dsheirer.sdrplay.device.RspDuoMode;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import io.github.dsheirer.sdrplay.error.DebugLevel;
import io.github.dsheirer.sdrplay.error.ErrorInformation;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParameters;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParametersFactory;
import io.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDRplay API wrapper.
 */
public class SDRplay
{
    public static final String SDRPLAY_API_LIBRARY_NAME = "sdrplay_api";
    public static final String SDRPLAY_API_PATH_LINUX = "/usr/local/lib/libsdrplay_api.so";
    public static final String SDRPLAY_API_PATH_MAC_OS = "/usr/local/lib/libsdrplay_api.dylib";
    public static final String SDRPLAY_API_PATH_WINDOWS = System.getenv("ProgramFiles") +
            "\\SDRplay\\API\\" + (System.getProperty("sun.arch.data.model").contentEquals("64") ? "x64" : "x86");
    public static final String JAVA_LIBRARY_PATH_KEY = "java.library.path";

    private static Logger mLog = LoggerFactory.getLogger(SDRplay.class);

    /**
     * Foreign memory allocation resource scope
     */
    private ResourceScope mResourceScope = ResourceScope.globalScope();

    /**
     * Foreign memory segment allocator for the resource scope
     */
    private SegmentAllocator mSegmentAllocator = SegmentAllocator.ofScope(mResourceScope);

    /**
     * Indicates if libsdrplay_api.xx library was found and loaded.
     */
    private boolean mSdrplayLibraryLoaded;

    /**
     * Indicates if the library is available, meaning that the host system library was loaded AND it supports the
     * correct API version
     */
    private boolean mAvailable;

    /**
     * RSP tuner devices (potentially) available for use
     */
    private List<Device> mDevices = new ArrayList<>();

    /**
     * Map of (reusable) callback functions for each device.
     */
    private Map<Device, CallbackFunctions> mDeviceCallbackFunctionsMap = new HashMap<>();

    /**
     * Constructs an instance of the SDRPLay API
     */
    public SDRplay()
    {
        mSdrplayLibraryLoaded = loadLibrary();

        if(mSdrplayLibraryLoaded)
        {
            Status openStatus = open();
            mAvailable = openStatus.success() && getVersion().isSupported();
        }
        else
        {
            mAvailable = false;
        }

        if(isAvailable())
        {
            //TODO: this class should maintain a list of devices and it should register as a listener for the device
            //to detect when the device is disconnected, so that the device can be removed from the list, in addition
            //to providing that event to any other users of the device.

            mLog.info("Loading Devices ...");
            loadDevices();
        }
        else
        {
            mLog.info("No devices loaded.  Detected API Version: " + getVersion());
        }
    }

    /**
     * Resource scope for this API instance
     */
    private ResourceScope getResourceScope()
    {
        return mResourceScope;
    }

    /**
     * Attempts to load the SDRPlay API library from the local system.
     *
     * @return true if library was loaded successfully.
     */
    private boolean loadLibrary()
    {
        try
        {
            String library = getSDRplayLibraryPath();
            mLog.info("Loading SDRplay Library from default install path: " + library);
            System.load(library);
            mLog.info("SDRPlay API library loaded");
            return true;
        }
        catch(Throwable t)
        {
            mLog.info("Unable to load SDRplay library from default install path.  Loading from java system library path");

            try
            {
                System.loadLibrary(SDRPLAY_API_LIBRARY_NAME);
                return true;
            }
            catch(Throwable t2)
            {
                String name = System.mapLibraryName(SDRPLAY_API_LIBRARY_NAME);
                mLog.warn("SDRPlay API library not found/installed on this system.  Ensure the API is installed either " +
                        "in the default install location or the install location is included in the " +
                        "'java.library.path' JVM property contains path to the library file [" + name +
                        "].  Current library path property contents: " + System.getProperty(JAVA_LIBRARY_PATH_KEY), t);
            }
        }

        return false;
    }

    /**
     * Loads the list of devices from the API
     */
    public void loadDevices()
    {
        //Get a version-correct array of DeviceT structures
        MemorySegment devicesArray = DeviceFactory.createForeignDeviceArray(getVersion(), mSegmentAllocator);

        MemorySegment deviceCount = mSegmentAllocator.allocate(CLinker.C_INT, 0);

        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_GetDevices(devicesArray, deviceCount,
                sdrplay_api_h.SDRPLAY_MAX_DEVICES()));

        if(status.success())
        {
            int count = MemoryAccess.getInt(deviceCount);
            mDevices.clear();
            mDevices.addAll(DeviceFactory.parseDevices(getVersion(), SDRplay.this, devicesArray, count));
            mLog.info("Loaded Device Count: " + mDevices.size());
        }
        else
        {
            mLog.error("Couldn't load devices.  Status: " + status);
        }
    }

    /**
     * List of RSP tuner device descriptors available from this SDRplay instance.
     */
    public List<DeviceDescriptor> getDevices()
    {
        List<DeviceDescriptor> descriptors = new ArrayList<>();

        for(Device device : mDevices)
        {
            descriptors.add(getDeviceDescriptor(device));
        }

        return Collections.unmodifiableList(descriptors);
    }

    /**
     * Find an RSP device descriptor by serial number.
     * @param serialNumber to search for
     * @return matching device descriptor, or null.
     */
    public DeviceDescriptor getDevice(String serialNumber)
    {
        for(DeviceDescriptor deviceDescriptor: getDevices())
        {
            if(deviceDescriptor.matches(serialNumber))
            {
                return deviceDescriptor;
            }
        }

        return null;
    }

    /**
     * Creates a device descriptor for the specified device
     *
     * @param device to describe
     * @return descriptor
     */
    private DeviceDescriptor getDeviceDescriptor(Device device)
    {
        List<DeviceSelectionMode> deviceSelectionModes = new ArrayList<>();

        if(device.getDeviceType().equals(DeviceType.RSPduo) && device instanceof RspDuoDevice rsp)
        {
            RspDuoMode mode = rsp.getRspDuoMode();
            TunerSelect tunerSelect = rsp.getTunerSelect();

            switch(mode)
            {
                case SLAVE -> {
                    if(tunerSelect.equals(TunerSelect.TUNER_1))
                    {
                        deviceSelectionModes.add(DeviceSelectionMode.SLAVE_TUNER_1);
                    }
                    else if(tunerSelect.equals(TunerSelect.TUNER_2))
                    {
                        deviceSelectionModes.add(DeviceSelectionMode.SLAVE_TUNER_2);
                    }
                }
                case MASTER -> {
                    if(tunerSelect.equals(TunerSelect.TUNER_1))
                    {
                        deviceSelectionModes.add(DeviceSelectionMode.MASTER_TUNER_1);
                    }
                    else if(tunerSelect.equals(TunerSelect.TUNER_2))
                    {
                        deviceSelectionModes.add(DeviceSelectionMode.MASTER_TUNER_2);
                    }
                }
                case UNKNOWN -> {
                    //All duo modes are available
                    deviceSelectionModes.addAll(Arrays.stream(DeviceSelectionMode.values()).toList());
                }
            }

        }
        else
        {
            //The only duo mode valid for non-RSPduo devices is single tuner mode for tuner 1
            deviceSelectionModes.add(DeviceSelectionMode.SINGLE_TUNER_1);
        }

        return new DeviceDescriptor(device, deviceSelectionModes);
    }

    /**
     * Finds the first device that matches the specified device type.
     *
     * @param deviceType to find
     * @return the specified device type or null.
     */
    public DeviceDescriptor getDevice(DeviceType deviceType)
    {
        for(DeviceDescriptor deviceDescriptor : getDevices())
        {
            if(deviceDescriptor.getDeviceType() == deviceType)
            {
                return deviceDescriptor;
            }
        }

        return null;
    }

    //TODO: can we segment off the API calls that should only used by/from the device instance, so that a user of
    //TODO: this API doesn't have access to those methods?  Thinking of using a private final inner class that can be
    //TODO: passed to the device constructor.

    /**
     * Selects the device for exclusive use.  This method is invoked by the device instance.
     *
     * @param device to select
     * @param memorySegment of the device in foreign memory
     * @throws SDRplayException if the device argument was not created by this API instance or if unable to lock or
     * unlock the device API for exclusive use, or if unable to select the device.
     */
    public void select(Device device, MemorySegment memorySegment) throws SDRplayException
    {
        checkValidDevice(device);

        lockDeviceApi();

        Status selectStatus = Status.fromValue(sdrplay_api_h.sdrplay_api_SelectDevice(memorySegment));

        unlockDeviceApi();

        if(selectStatus.fail())
        {
            throw new SDRplayException("Unable to select the device", selectStatus);
        }
    }

    /**
     * Configures the device for the specified selection mode and selects it for use.
     *
     * @param deviceDescriptor to select
     * @param deviceSelectionMode to configure
     * @throws SDRplayException if the device cannot be selected for the specified selection mode
     */
    public Device select(DeviceDescriptor deviceDescriptor, DeviceSelectionMode deviceSelectionMode) throws SDRplayException
    {
        Device device = deviceDescriptor.getDevice();

        if(deviceDescriptor.supports(deviceSelectionMode))
        {
            if(device instanceof RspDuoDevice duo)
            {
                duo.setRspDuoMode(deviceSelectionMode.getRspDuoMode());
                duo.setTunerSelect(deviceSelectionMode.getTunerSelect());

                //In master mode, we have to set the sample rate here, before we select the device
                if(deviceSelectionMode.isMasterMode())
                {
                    duo.setRspDuoSampleFrequency(8_000_000);
                }

                if(deviceSelectionMode.equals(DeviceSelectionMode.DUAL_INDEPENDENT_TUNERS))
                {
                    //Select the first device as Master/Tuner 1.
                    device.select();

                    //Create a second API instance and find the matching device by serial number
                    SDRplay api2 = new SDRplay();
                    DeviceDescriptor deviceDescriptor2 = api2.getDevice(deviceDescriptor.getSerialNumber());

                    if(deviceDescriptor2 != null && deviceDescriptor2.getDevice() instanceof RspDuoDevice slaveDevice)
                    {
                        slaveDevice.setRspDuoMode(RspDuoMode.SLAVE);
                        slaveDevice.setTunerSelect(TunerSelect.TUNER_2);
                        slaveDevice.select();
                        return new RspDuoDualIndependentTunerDevice(duo, slaveDevice);
                    }

                    //Release the first instance and throw an exception to indicate we couldn't configure as requested
                    device.release();
                    throw new SDRplayException("Unable to acquire second device instance for RSPduo to configure as " +
                            "dual-independent tuners");
                }
            }

            device.select();
            return device;
        }

        throw new SDRplayException("Selection mode [" + deviceSelectionMode + "] is not supported by " + deviceDescriptor);
    }

    /**
     * Releases the device from exclusive use.  This method is invoked by the device instance.
     *
     * @param device to release
     * @param memorySegment of the device in foreign memory
     * @throws SDRplayException if the device argument was not created by this API instance or if unable to release
     * the device
     */
    public void release(Device device, MemorySegment memorySegment) throws SDRplayException
    {
        checkValidDevice(device);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_ReleaseDevice(memorySegment));

        if(status.fail())
        {
            throw new SDRplayException("Unable to release the device from exclusive use.", status);
        }
    }

    /**
     * Retrieves the initial composite parameters for each device.  This should only be invoked once, on
     * startup, for each device.  Changes made to the device parameters should invoke update() method to apply changes.
     *
     * @param device to load parameters
     * @param deviceHandle to device
     * @return constructed device composite paramaters
     */
    public CompositeParameters getCompositeParameters(Device device, MemoryAddress deviceHandle) throws SDRplayException
    {
        checkValidDevice(device);

        //Allocate a pointer that the api will fill with the memory address of the device parameters in memory.
        MemorySegment pointer = mSegmentAllocator.allocate(CLinker.C_POINTER);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_GetDeviceParams(deviceHandle, pointer));

        if(status.success())
        {
            MemoryAddress memoryAddress = MemoryAccess.getAddress(pointer);
            MemorySegment memorySegment = sdrplay_api_DeviceT.ofAddress(memoryAddress, mResourceScope);
            return CompositeParametersFactory.create(device.getDeviceType(), memorySegment, mResourceScope);
        }
        else
        {
            throw new SDRplayException("Error retrieving device composite parameters", status);
        }
    }

    /**
     * Initializes a device for use.
     *
     * @param device to initialize
     * @param deviceHandle to the device
     * @param callbackFunctions to receive stream data from A and (optionally) B channels and events.
     * @throws SDRplayException if the device is not selected of if unable to init the device
     */
    private void init(Device device, MemoryAddress deviceHandle, MemorySegment callbackFunctions) throws SDRplayException
    {
        checkValidDevice(device);

        //Since we don't need/use the callback context ... setup as a pointer to the callback functions
        MemorySegment contextPointer = mSegmentAllocator.allocate(CLinker.C_POINTER, callbackFunctions);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Init(deviceHandle, callbackFunctions, contextPointer));

        if(!status.success())
        {
            throw new SDRplayException("Error while initializing device", status);
        }
    }

    /**
     * Initializes a device for use.
     *
     * @param device to initialize
     * @param deviceHandle to the device
     * @param eventListener to receive events for this device
     * @param streamAListener to receive samples for stream A / tuner 1
     * @param streamBListener to receive samples for stream B / tuner 2 (if applicable)
     * @throws SDRplayException if the device is not selected of if unable to init the device
     */
    public void init(Device device, MemoryAddress deviceHandle, IDeviceEventListener eventListener,
                     IStreamListener streamAListener, IStreamListener streamBListener) throws SDRplayException
    {
        CallbackFunctions callbackFunctions = mDeviceCallbackFunctionsMap.get(device);

        if(callbackFunctions == null)
        {
            callbackFunctions = new CallbackFunctions(getResourceScope(), eventListener, streamAListener,
                    streamBListener, device.getStreamCallbackListener());
            mDeviceCallbackFunctionsMap.put(device, callbackFunctions);
        }
        else
        {
            callbackFunctions.setDeviceEventListener(eventListener);
            callbackFunctions.setStreamAListener(streamAListener);
            callbackFunctions.setStreamBListener(streamBListener);
        }

        init(device, deviceHandle, callbackFunctions.getCallbackFunctionsMemorySegment());
    }

    /**
     * Un-Initializes a device from use.
     *
     * @param device to un-initialize
     * @param deviceHandle to the device
     * @throws SDRplayException if error during uninit or if device is not selected
     */
    public void uninit(Device device, MemoryAddress deviceHandle) throws SDRplayException
    {
        checkValidDevice(device);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Uninit(deviceHandle));

        if(status.fail() && status != Status.NOT_INITIALIZED)
        {
            throw new SDRplayException("Error while un-initializing device", status);
        }
    }

    /**
     * Applies updates made to the device parameters.  The device parameter that was updated is specified in the
     * update reason.
     * <p>
     * Note: this method is synchronized to prevent multiple threads from attempting to send update requests
     * concurrently, which will cause a failed request.
     *
     * @param device to update
     * @param deviceHandle for the device
     * @param tunerSelect identifies which tuner to apply the updates
     * @param updateReasons identifying what was updated
     * @throws SDRplayException if the device is not selected, or if unable to update the device parameters
     */
    public synchronized void update(Device device, MemoryAddress deviceHandle, TunerSelect tunerSelect,
                                    UpdateReason... updateReasons) throws SDRplayException
    {
        checkValidDevice(device);

        int reasons = UpdateReason.getReasons(updateReasons);
        int extendedReasons = UpdateReason.getExtendedReasons(updateReasons);

        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Update(deviceHandle, tunerSelect.getValue(),
                reasons, extendedReasons));

        if(status.fail())
        {
            throw new SDRplayUpdateException(status, Arrays.stream(updateReasons).toList());
        }
    }

    /**
     * Retrieve error information for the last error for the specified device.
     *
     * @param device to check
     * @param deviceSegment for the device
     * @return error information
     */
    private ErrorInformation getLastError(Device device, MemorySegment deviceSegment)
    {
        MemoryAddress errorAddress = sdrplay_api_h.sdrplay_api_GetLastError(deviceSegment);
        MemorySegment errorSegment = errorAddress.asSegment(sdrplay_api_ErrorInfoT.sizeof(), mResourceScope);
        return new ErrorInformation(errorSegment);
    }

    /**
     * Sets the debug level logging for the specified device
     *
     * @param device to set debug level on
     * @param deviceHandle for the device
     * @param debugLevel to set
     * @throws SDRplayException if the device is not selected or if unable to set/change the debug level.
     */
    public void setDebugLevel(Device device, MemoryAddress deviceHandle, DebugLevel debugLevel) throws SDRplayException
    {
        checkValidDevice(device);

        Status status = Status.UNKNOWN;

        if(getVersion() == Version.V3_07)
        {
            //V3.07 used a debug level argument
            status = Status.fromValue(sdrplay_api_h.sdrplay_api_DebugEnable(deviceHandle, debugLevel.getValue()));
        }
        else if(getVersion().gte(Version.V3_08))
        {
            //V3.08+ uses a 0:1 flag to enable debug logging.  The method signature didn't change -- still takes an integer
            boolean enable = debugLevel != DebugLevel.DISABLE;
            status = Status.fromValue(sdrplay_api_h.sdrplay_api_DebugEnable(deviceHandle, Flag.of(enable)));
        }

        if(status.fail())
        {
            throw new SDRplayException("Unable to set debug level", status);
        }
    }

    /**
     * Checks that the device was constructed by this API instance and continues to be a usable device.
     *
     * @param device to check
     */
    private void checkValidDevice(Device device) throws SDRplayException
    {
        if(!mDevices.contains(device))
        {
            throw new SDRplayException("Unrecognized device argument -- must be device created by this API instance");
        }
    }

    /**
     * Indicates if the SDRplay API is available and that the API library has been located and loaded for use and
     * it the API version is supported by this jsdrplay library.
     */
    public boolean isAvailable()
    {
        return mAvailable;
    }

    /**
     * Opens the API service.  MUST be invoked before accessing any of the API functions/methods.
     */
    private Status open()
    {
        return Status.fromValue(sdrplay_api_h.sdrplay_api_Open());
    }

    /**
     * Closes the API service.  MUST be invoked before shutdown, after all SDRPlay API operations are completed.
     */
    public void close() throws SDRplayException
    {
        Status closeStatus = Status.fromValue(sdrplay_api_h.sdrplay_api_Close());
        mSdrplayLibraryLoaded = false;
        mAvailable = false;

        if(closeStatus.fail())
        {
            throw new SDRplayException("Unable to close the API", closeStatus);
        }
    }

    /**
     * Identifies the API version.
     * Note: if the library is not found or loaded, or if the API version is not a supported version, this method
     * returns UNKNOWN
     *
     * @return version.
     * @throws SDRplayException if unable to get the API version.
     */
    public Version getVersion()
    {
        if(mSdrplayLibraryLoaded)
        {
            SegmentAllocator allocator = SegmentAllocator.ofScope(mResourceScope);
            MemorySegment memorySegment = allocator.allocate(CLinker.C_FLOAT, 0.0f);
            Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_ApiVersion(memorySegment));

            if(status.success())
            {
                float apiVersion = MemoryAccess.getFloat(memorySegment);
                return Version.fromValue(apiVersion);
            }
        }

        return Version.UNKNOWN;
    }

    /**
     * Attempts to lock the API for exclusive use of the current application. Once locked, no other applications
     * will be able to use the API. Typically used to lock the API prior to calling sdrplay_api_GetDevices() to
     * ensure only one application can select a given device. After completing device selection using
     * sdrplay_api_SelectDevice(), sdrplay_api_UnlockDeviceApi() can be used to release the API. May also
     * be used prior to calling sdrplay_api_ReleaseDevice() if it is necessary to reselect the same device.
     */
    private void lockDeviceApi() throws SDRplayException
    {
        if(isAvailable())
        {
            Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_LockDeviceApi());
            if(status.fail())
            {
                throw new SDRplayException("Unable to lock Device API for exclusive use", status);
            }
        }
        else
        {
            throw new SDRplayException("API is unavailable", Status.API_UNAVAILABLE);
        }
    }

    /**
     * Unlocks the device from exclusive access.
     *
     * @throws SDRplayException if unable to unlock the Device API
     */
    private void unlockDeviceApi() throws SDRplayException
    {
        if(isAvailable())
        {
            Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_UnlockDeviceApi());

            if(status.fail())
            {
                throw new SDRplayException("Unable to unlock Device API", status);
            }
        }
        else
        {
            throw new SDRplayException("API is unavailable", Status.API_UNAVAILABLE);
        }
    }

    /**
     * Identifies the java library path for the sdrplay api library at runtime.
     */
    public static String getSDRplayLibraryPath()
    {
        if(SystemUtils.IS_OS_WINDOWS)
        {
            return SDRPLAY_API_PATH_WINDOWS;
        }
        else if(SystemUtils.IS_OS_LINUX)
        {
            return SDRPLAY_API_PATH_LINUX;
        }
        else if(SystemUtils.IS_OS_MAC_OSX)
        {
            return SDRPLAY_API_PATH_MAC_OS;
        }

        mLog.error("Unrecognized operating system.  Cannot identify sdrplay api library path");
        return "";
    }
}
