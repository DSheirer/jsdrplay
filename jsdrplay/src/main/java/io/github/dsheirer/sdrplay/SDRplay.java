package io.github.dsheirer.sdrplay;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DeviceParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DeviceT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_ErrorInfoT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_RxChannelParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_h;
import io.github.dsheirer.sdrplay.device.Device;
import io.github.dsheirer.sdrplay.device.DeviceFactory;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import io.github.dsheirer.sdrplay.error.DebugLevel;
import io.github.dsheirer.sdrplay.error.ErrorInformation;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParameters;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParametersFactory;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.SegmentAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SDRplay
{
    public static final String SDRPLAY_API_LIBRARY_NAME = "sdrplay_api";

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
     * Constructs an instance of the SDRPLay API
     */
    public SDRplay()
    {
        mSdrplayLibraryLoaded = loadLibrary();

        if(mSdrplayLibraryLoaded)
        {
            Status openStatus = open();
            mLog.info("Open Status: " + openStatus);
            mAvailable = openStatus.success() && getAPIVersion().isSupported();
        }
        else
        {
            mAvailable = false;
        }
        //TODO: add all java.library.path for linux, windows, mac, etc.

        //TODO: this class should maintain a list of devices and it should register as a listener for the device
        //to detect when the device is disconnected, so that the device can be removed from the list, in addition
        //to providing that event to any other users of the device.

        mLog.info("Loading Devices ...");
        loadDevices();
    }

    /**
     * Attempts to load the SDRPlay API library from the local system.
     * @return true if library was loaded successfully.
     */
    private boolean loadLibrary()
    {
        try
        {
            System.loadLibrary(SDRPLAY_API_LIBRARY_NAME);
            mLog.info("SDRPlay API library loaded");
            return true;
        }
        catch(Exception e)
        {
            String name = System.mapLibraryName(SDRPLAY_API_LIBRARY_NAME);
            mLog.warn("SDRPlay API library not found/installed on this system.  Ensure the API is installed and " +
                    "the 'java.library.path' JVM property contains path to the library file [" + name + "].  Current " +
                    "property contents: " + System.getProperty("java.library.path"));
        }

        return false;
    }

    private void loadDevices()
    {
        SegmentAllocator allocator = SegmentAllocator.ofScope(mResourceScope);
        MemorySegment devicesArray = sdrplay_api_DeviceT.allocateArray(sdrplay_api_h.SDRPLAY_MAX_DEVICES(), allocator);
        MemorySegment deviceCount = allocator.allocate(CLinker.C_INT, 0);

        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_GetDevices(devicesArray, deviceCount,
                sdrplay_api_h.SDRPLAY_MAX_DEVICES()));

        if(status.success())
        {
            int count = MemoryAccess.getInt(deviceCount);
            devicesArray.elements(sdrplay_api_DeviceT.$LAYOUT()).limit(count).forEach(memorySegment ->
            {

                mDevices.add(DeviceFactory.create(SDRplay.this, memorySegment));
            });

            mLog.info("Loaded Device Count: " + mDevices.size());
        }
        else
        {
            mLog.error("Couldn't load devices.  Status: " + status);
        }
    }

    /**
     * List of RSP tuner devices available from this SDRplay instance.
     */
    public List<Device> getDevices()
    {
        return Collections.unmodifiableList(mDevices);
    }

    //TODO: can we segment off the API calls that should only used by/from the device instance, so that a user of
    //TODO: this API doesn't have access to those methods?  Thinking of using a private final inner class that can be
    //TODO: passed to the device constructor.

    /**
     * Selects the device for exclusive use.  This method is invoked by the device instance.
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
     * Releases the device from exclusive use.  This method is invoked by the device instance.
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
     * @param device to load parameters
     * @param deviceHandle to device
     * @return constructed device composite paramaters
     */
    public CompositeParameters getCompositeParameters(Device device, MemoryAddress deviceHandle) throws SDRplayException
    {
        checkValidDevice(device);

        //Create the device parameters sub-structures
        MemorySegment deviceParametersSegment = sdrplay_api_DevParamsT.allocate(mSegmentAllocator);
        MemorySegment rxChannelASegment = sdrplay_api_RxChannelParamsT.allocate(mSegmentAllocator);
        MemorySegment rxChannelBSegment = sdrplay_api_RxChannelParamsT.allocate(mSegmentAllocator);

        //Create the device composite parameters and assign the sub-structures
        MemorySegment compositeSegment = sdrplay_api_DeviceParamsT.allocate(mSegmentAllocator);
        sdrplay_api_DeviceParamsT.devParams$set(compositeSegment, deviceParametersSegment.address());
        sdrplay_api_DeviceParamsT.rxChannelA$set(compositeSegment, rxChannelASegment.address());
        sdrplay_api_DeviceParamsT.rxChannelB$set(compositeSegment, rxChannelBSegment.address());

        MemorySegment pointerToPointer = mSegmentAllocator.allocate(CLinker.C_POINTER, compositeSegment.address());
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_GetDeviceParams(deviceHandle, pointerToPointer));

        if(status.success())
        {
            return CompositeParametersFactory.create(device.getDeviceType(), compositeSegment, mResourceScope);
        }
        else
        {
            throw new SDRplayException("Error retrieving device composite parameters", status);
        }
    }

    /**
     * Initializes a device for use.
     * @param device to initialize
     * @param deviceHandle to the device
     * @param callbackFunctions to receive stream data from A and (optionally) B channels and events.
     * @throws SDRplayException if the device is not selected of if unable to init the device
     */
    public void init(Device device, MemoryAddress deviceHandle, MemorySegment callbackFunctions) throws SDRplayException
    {
        checkValidDevice(device);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Init(deviceHandle, callbackFunctions, null));

        if(!status.success())
        {
            throw new SDRplayException("Error while initializing device", status);
        }
    }

    /**
     * Un-Initializes a device from use.
     * @param device to un-initialize
     * @param deviceHandle to the device
     * @throws SDRplayException if error during uninit or if device is not selected
     */
    public void uninit(Device device, MemoryAddress deviceHandle) throws SDRplayException
    {
        checkValidDevice(device);
        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Uninit(deviceHandle));

        if(status.fail())
        {
            throw new SDRplayException("Error while un-initializing device", status);
        }
    }

    /**
     * Applies updates made to the device parameters.  The device parameter that was updated is specified in the
     * update reason.
     * @param device to update
     * @param deviceHandle for the device
     * @param tunerSelect identifies which tuner to apply the updates
     * @param updateReasons identifying what was updated
     * @throws SDRplayException if the device is not selected, or if unable to update the device parameters
     */
    public void update(Device device, MemoryAddress deviceHandle, TunerSelect tunerSelect, UpdateReason ... updateReasons)
                throws SDRplayException
    {
        checkValidDevice(device);

        int reasons = UpdateReason.getReasons(updateReasons);
        int extendedReasons = UpdateReason.getExtendedReasons(updateReasons);

        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_Update(deviceHandle, tunerSelect.getValue(),
                reasons, extendedReasons));

        if(status.fail())
        {
            throw new SDRplayException("Unable to update device parameters for update reasons: " + updateReasons, status);
        }
    }

    /**
     * Retrieve error information for the last error for the specified device.
     * @param device to check
     * @param deviceSegment for the device
     * @return error information
     */
    ErrorInformation getLastError(Device device, MemorySegment deviceSegment)
    {
        MemoryAddress errorAddress = sdrplay_api_h.sdrplay_api_GetLastError(deviceSegment);
        MemorySegment errorSegment = errorAddress.asSegment(sdrplay_api_ErrorInfoT.sizeof(), mResourceScope);
        return new ErrorInformation(errorSegment);
    }

    /**
     * Sets the debug level logging for the specified device
     * @param device to set debug level on
     * @param deviceHandle for the device
     * @param debugLevel to set
     * @throws SDRplayException if the device is not selected or if unable to set/change the debug level.
     */
    public void setDebugLevel(Device device, MemoryAddress deviceHandle, DebugLevel debugLevel) throws SDRplayException
    {
        checkValidDevice(device);

        Status status = Status.fromValue(sdrplay_api_h.sdrplay_api_DebugEnable(deviceHandle, debugLevel.getValue()));

        if(status.fail())
        {
            throw new SDRplayException("Unable to set debug level", status);
        }
    }

    /**
     * Checks that the device was constructed by this API instance and continues to be a usable device.
     * @param device to check
     */
    private void checkValidDevice(Device device) throws SDRplayException
    {
        if(!mDevices.contains(device))
        {
            throw new SDRplayException("Unrecognized device argument -- must be device instance created by this API instance");
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
     * @return version.
     * @throws SDRplayException if unable to get the API version.
     */
    public Version getAPIVersion()
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

    public static void main(String[] args)
    {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%-25(%d{yyyyMMdd HHmmss.SSS} [%thread]) %-5level %logger{30} - %msg");
        encoder.start();

        SDRplay sdrplay = new SDRplay();
        mLog.info("SDRplay Library Available: " + sdrplay.isAvailable());
        mLog.info("API Version: " + sdrplay.getAPIVersion());

        List<Device> devices = sdrplay.getDevices();

        for(Device device: devices)
        {
            try
            {
                mLog.info("Selecting Device: " + device);
                device.select();
                mLog.info("Releasing Device");
                device.release();
            }
            catch(SDRplayException se)
            {
                mLog.error("Error", se);
            }
        }

        try
        {
            sdrplay.close();
        }
        catch(SDRplayException se)
        {
            mLog.error("Error closing API", se);
        }

        mLog.info("Complete!");
    }
}
