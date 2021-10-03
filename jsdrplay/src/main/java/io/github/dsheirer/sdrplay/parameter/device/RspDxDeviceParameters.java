package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDxAntenna;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_RspDxParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-DX Device Parameters structure
 */
public class RspDxDeviceParameters extends DeviceParameters
{
    private MemorySegment mRspDxMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment.
     */
    public RspDxDeviceParameters(MemorySegment memorySegment)
    {
        super(memorySegment);
        mRspDxMemorySegment = sdrplay_api_DevParamsT.rspDxParams$slice(memorySegment);
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getRspDxMemorySegment()
    {
        return mRspDxMemorySegment;
    }

    /**
     * Indicates if High Dynamic Range (HDR) is enabled
     */
    public boolean isHdr()
    {
        return Flag.evaluate(sdrplay_api_RspDxParamsT.hdrEnable$get(getRspDxMemorySegment()));
    }

    /**
     * Enables or disables High Dynamic Range (HDR)
     */
    public void setHdr(boolean enable)
    {
        sdrplay_api_RspDxParamsT.hdrEnable$set(getRspDxMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return Flag.evaluate(sdrplay_api_RspDxParamsT.biasTEnable$get(getRspDxMemorySegment()));
    }

    /**
     * Enables or disables Bias-T
     */
    public void setBiasT(boolean enable)
    {
        sdrplay_api_RspDxParamsT.biasTEnable$set(getRspDxMemorySegment(), Flag.of(enable));
    }

    /**
     * RSPdx Antenna Setting
     */
    public RspDxAntenna getRspDxAntenna()
    {
        return RspDxAntenna.fromValue(sdrplay_api_RspDxParamsT.antennaSel$get(getRspDxMemorySegment()));
    }

    /**
     * Selects the RSPdx Antenna
     */
    public void setRspDxAntenna(RspDxAntenna rspDxAntenna)
    {
        sdrplay_api_RspDxParamsT.antennaSel$set(getRspDxMemorySegment(), rspDxAntenna.getValue());
    }

    /**
     * Indicates if RF notch is enabled
     */
    public boolean isRfNotch()
    {
        return Flag.evaluate(sdrplay_api_RspDxParamsT.rfNotchEnable$get(getRspDxMemorySegment()));
    }

    /**
     * Enables or disables RF notch
     */
    public void setRfNotch(boolean enable)
    {
        sdrplay_api_RspDxParamsT.rfNotchEnable$set(getRspDxMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if the RF DAB notch is enabled
     */
    public boolean isRfDabNotch()
    {
        return Flag.evaluate(sdrplay_api_RspDxParamsT.rfDabNotchEnable$get(getRspDxMemorySegment()));
    }

    /**
     * Enables or disables the RF DAB notch
     */
    public void setRfDabNotch(boolean enable)
    {
        sdrplay_api_RspDxParamsT.rfDabNotchEnable$set(getRspDxMemorySegment(), Flag.of(enable));
    }
}
