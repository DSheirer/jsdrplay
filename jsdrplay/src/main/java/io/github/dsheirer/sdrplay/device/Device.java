package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParameters;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract device structure (sdrplay_api_DeviceT)
 */
public abstract class Device<T extends CompositeParameters, R extends RspTuner>
{
    private static final Logger mLog = LoggerFactory.getLogger(Device.class);

    private SDRplay mSDRplay;
    private Version mVersion;
    private MemorySegment mDeviceMemorySegment;
    private DeviceType mDeviceType;

    private String mSerialNumber;
    private boolean mValid;
    private boolean mSelected = false;
    private T mCompositeParameters;

    /**
     * Constructs an SDRPlay device from the foreign memory segment
     * @param sdrPlay api instance that created this device
     * @param deviceMemorySegment
     */
    public Device(SDRplay sdrPlay, Version version, MemorySegment deviceMemorySegment, DeviceType deviceType)
    {
        mSDRplay = sdrPlay;
        mVersion = version;
        mDeviceMemorySegment = deviceMemorySegment;
        mDeviceType = deviceType;

        MemorySegment serialSegment = sdrplay_api_DeviceT.SerNo$slice(mDeviceMemorySegment);
        byte[] serialBytes = new byte[sdrplay_api_h.SDRPLAY_MAX_SER_NO_LEN()];
        for(int x = 0; x < sdrplay_api_h.SDRPLAY_MAX_SER_NO_LEN(); x++)
        {
            serialBytes[x] = MemoryAccess.getByteAtOffset(serialSegment, x);
        }
        mSerialNumber = new String(serialBytes).trim();

        if(mVersion.gte(Version.V3_08))
        {
            mValid = true;//TODO: use jextract to create the version 3.08 class for DeviceT
        }
    }

    /**
     * API that owns this device
     */
    protected SDRplay getAPI()
    {
        return mSDRplay;
    }

    /**
     * Loads the device parameters for this device.  Subsequent calls once the parameters are created are ignored.
     * @throws SDRplayException if the device is not selected or if there is an issue loading the parameters
     */
    private void loadDeviceParameters() throws SDRplayException
    {
        if(selected() && !hasCompositeParameters())
        {
            mCompositeParameters = (T)getAPI().getCompositeParameters(Device.this, getDeviceHandle());
        }
    }

    /**
     * Selects this device for exclusive use and loads the device composite parameters.
     * @throws SDRplayException if unable to select the device or if unable to load the composite parameters for use.
     */
    public void select() throws SDRplayException
    {
        if(!selected())
        {
            getAPI().select(Device.this, getDeviceMemorySegment());
            mSelected = true;
            loadDeviceParameters();
        }
    }

    /**
     * Indicates if this device has been selected via the SDRplay api
     */
    private boolean selected()
    {
        return mSelected;
    }

    /**
     * Indicates if the device is valid and ready for use
     */
    public boolean isValid()
    {
        return mValid;
    }

    /**
     * Releases this device from exclusive use.
     */
    public void release() throws SDRplayException
    {
        if(selected())
        {
            mSelected = false;
            getAPI().release(Device.this, getDeviceMemorySegment());
        }
    }

    /**
     * Updates this device after parameter change
     * @throws SDRplayException if unable to apply updates
     */
    protected void update(TunerSelect tunerSelect, UpdateReason ... updateReasons) throws SDRplayException
    {
        getAPI().update(Device.this, getDeviceHandle(), tunerSelect, updateReasons);
    }

    /**
     * Tuner selection.
     * @return tuner select.  Defaults to TUNER_1 for all but the RSPduo
     */
    public TunerSelect getTunerSelect()
    {
        return TunerSelect.TUNER_1;
    }

    /**
     * Foreign memory segment representing this device.
     */
    protected MemorySegment getDeviceMemorySegment()
    {
        return mDeviceMemorySegment;
    }

    /**
     * Handle to this device.
     *
     * Note: this device must be selected for exclusive use before you can access this handle.
     *
     * @throws SDRplayException if this method is accessed before the device has been successfully selected
     */
    MemoryAddress getDeviceHandle() throws SDRplayException
    {
        if(!selected())
        {
            throw new SDRplayException("This device must be selected for exclusive use before accessing/using the " +
                    "device handle");
        }

        return sdrplay_api_DeviceT.dev$get(mDeviceMemorySegment);
    }

    /**
     * Tuner 1 for this device
     * @return tuner 1 appropriate for the device type
     * @throws SDRplayException for various reasons include device not selected or API unavailable
     */
    public abstract R getTuner1() throws SDRplayException;

    /**
     * Composite parameters for this device
     */
    T getCompositeParameters()
    {
        return mCompositeParameters;
    }

    /**
     * Sets the composite parameters
     * @param compositeParameters
     */
    public void setCompositeParameters(T compositeParameters)
    {
        mCompositeParameters = compositeParameters;
    }

    /**
     * Indicates if this device has composite parameters
     */
    boolean hasCompositeParameters()
    {
        return mCompositeParameters != null;
    }

    /**
     * Indicates if this device is available and has been selected for exclusive use.
     */
    public boolean isSelected()
    {
        return mSelected;
    }

    /**
     * Device type
     */
    public DeviceType getDeviceType()
    {
        return mDeviceType;
    }

    /**
     * Serial number
     */
    public String getSerialNumber()
    {
        return mSerialNumber;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SDRPplay Device").append("\n");
        sb.append("\tType: ").append(getDeviceType()).append("\n");
        sb.append("\tSerial Number: ").append(getSerialNumber()).append("\n");
        sb.append("\tSelected: ").append(isSelected());
        if(hasCompositeParameters())
        {
            sb.append("\t").append(getCompositeParameters());
        }

        return sb.toString();
    }
}
