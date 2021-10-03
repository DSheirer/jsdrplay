package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_Rsp1aParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-1A Device Parameters structure
 */
public class Rsp1aDeviceParameters extends DeviceParameters
{
    private MemorySegment mRsp1AMemorySegment;

    /**
     * Constructs an instance
     * @param memorySegment for this structure
     */
    public Rsp1aDeviceParameters(MemorySegment memorySegment)
    {
        super(memorySegment);
        mRsp1AMemorySegment = sdrplay_api_DevParamsT.rsp1aParams$slice(memorySegment);
    }

    /**
     * Foreign memory segment representing this structure
     */
    private MemorySegment getRsp1AMemorySegment()
    {
        return mRsp1AMemorySegment;
    }

    /**
     * Indicates if RF notch is enabled.
     */
    public boolean isRFNotch()
    {
        return Flag.evaluate(sdrplay_api_Rsp1aParamsT.rfNotchEnable$get(getRsp1AMemorySegment()));
    }

    /**
     * Enables or disables the RF notch.
     */
    public void setRFNotch(boolean enable)
    {
        sdrplay_api_Rsp1aParamsT.rfNotchEnable$set(getRsp1AMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if DAB RF notch is enabled
     */
    public boolean isRfDabNotch()
    {
        return Flag.evaluate(sdrplay_api_Rsp1aParamsT.rfDabNotchEnable$get(getRsp1AMemorySegment()));
    }

    /**
     * Enables or disables the DAB RF notch
     */
    public void setRfDabNotch(boolean enable)
    {
        sdrplay_api_Rsp1aParamsT.rfNotchEnable$set(getRsp1AMemorySegment(), Flag.of(enable));
    }
}
