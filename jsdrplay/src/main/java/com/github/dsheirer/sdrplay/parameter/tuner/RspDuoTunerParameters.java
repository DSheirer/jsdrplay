package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RspDuoTunerParamsT;
import com.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSPduo Tuner Parameters structure
 */
public class RspDuoTunerParameters extends TunerParameters
{
    private MemorySegment mRspDuoMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment
     * @param tunerParametersMemorySegment of foreign memory structure
     * @param rspDuoMemorySegment of foreign memory structure
     */
    RspDuoTunerParameters(MemorySegment tunerParametersMemorySegment, MemorySegment rspDuoMemorySegment)
    {
        super(tunerParametersMemorySegment);
        mRspDuoMemorySegment = rspDuoMemorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getRspDuoMemorySegment()
    {
        return mRspDuoMemorySegment;
    }

    /**
     * Indicates if the Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return Flag.evaluate(sdrplay_api_RspDuoTunerParamsT.biasTEnable$get(getRspDuoMemorySegment()));
    }

    /**
     * Enables or disables the Bias-T
     */
    public void setBiasT(boolean enable)
    {
        sdrplay_api_RspDuoTunerParamsT.biasTEnable$set(getRspDuoMemorySegment(), Flag.of(enable));
    }

    /**
     * Tuner 1 AM port setting
     */
    public RspDuoAmPort getTuner1AmPort()
    {
        return RspDuoAmPort.fromValue(sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$get(getRspDuoMemorySegment()));
    }

    /**
     * Set Tuner 1 AM port setting
     */
    public void setTuner1AmPort(RspDuoAmPort amPort)
    {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$set(getRspDuoMemorySegment(), amPort.getValue());
    }

    /**
     * Indicates if Tuner 1 AM notch is enabled
     */
    public boolean isTuner1AmNotch()
    {
        return Flag.evaluate(sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$get(getRspDuoMemorySegment()));
    }

    /**
     * Enables or disables the Tuner 1 AM notch
     */
    public void setTuner1AmNotch(boolean enable)
    {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$set(getRspDuoMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if the RF notch is enabled
     */
    public boolean isRfNotch()
    {
        return Flag.evaluate(sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$get(getRspDuoMemorySegment()));
    }

    /**
     * Enables or disables the RF notch
     */
    public void setRfNotch(boolean enable)
    {
        sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$set(getRspDuoMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if the RF DAB notch is enabled
     */
    public boolean isRfDabNotch()
    {
        return Flag.evaluate(sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$get(getRspDuoMemorySegment()));
    }

    /**
     * Enables or disables the RF DAB notch
     */
    public void setRfDabNotch(boolean enable)
    {
        sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$set(getRspDuoMemorySegment(), Flag.of(enable));
    }
}
