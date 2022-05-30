package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_Rsp2TunerParamsT;
import io.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP2 Tuner Parameters structure
 */
public class Rsp2TunerParameters extends TunerParameters
{
    private MemorySegment mRsp2MemorySegment;

    public Rsp2TunerParameters(MemorySegment tunerParametersMemorySegment, MemorySegment rsp2MemorySegment)
    {
        super(tunerParametersMemorySegment);
        mRsp2MemorySegment = rsp2MemorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getRsp2MemorySegment()
    {
        return mRsp2MemorySegment;
    }

    /**
     * Indicates if the Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return Flag.evaluate(sdrplay_api_Rsp2TunerParamsT.biasTEnable$get(getRsp2MemorySegment()));
    }

    /**
     * Enables or disables the Bias-T
     */
    public void setBiasT(boolean enable)
    {
        sdrplay_api_Rsp2TunerParamsT.biasTEnable$set(getRsp2MemorySegment(), Flag.of(enable));
    }

    /**
     * Current AM port setting
     */
    public Rsp2AmPort getAmPort()
    {
        return Rsp2AmPort.fromValue(sdrplay_api_Rsp2TunerParamsT.amPortSel$get(getRsp2MemorySegment()));
    }

    /**
     * Sets the AM port to use
     */
    public void setAmPort(Rsp2AmPort amPort)
    {
        sdrplay_api_Rsp2TunerParamsT.amPortSel$set(getRsp2MemorySegment(), amPort.getValue());
    }

    /**
     * Antenna setting
     */
    public Rsp2Antenna getAntenna()
    {
        return Rsp2Antenna.fromValue(sdrplay_api_Rsp2TunerParamsT.antennaSel$get(getRsp2MemorySegment()));
    }

    /**
     * Sets the antenna
     */
    public void setAntenna(Rsp2Antenna rsp2Antenna)
    {
        sdrplay_api_Rsp2TunerParamsT.antennaSel$set(getRsp2MemorySegment(), rsp2Antenna.getValue());
    }

    /**
     * Indicates if the RF notch is enabled
     */
    public boolean isRfNotch()
    {
        return Flag.evaluate(sdrplay_api_Rsp2TunerParamsT.rfNotchEnable$get(getRsp2MemorySegment()));
    }

    /**
     * Enables or disables the RF notch
     */
    public void setRfNotch(boolean enable)
    {
        sdrplay_api_Rsp2TunerParamsT.rfNotchEnable$set(getRsp2MemorySegment(), Flag.of(enable));
    }
}
