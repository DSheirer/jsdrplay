package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_Rsp1aTunerParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-1A Tuner Parameters structure
 */
public class Rsp1aTunerParameters extends TunerParameters
{
    private MemorySegment mRsp1aMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment.
     */
    public Rsp1aTunerParameters(MemorySegment tunerParametersMemorySegment, MemorySegment rsp1aMemorySegment)
    {
        super(tunerParametersMemorySegment);
        mRsp1aMemorySegment = rsp1aMemorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getRsp1aMemorySegment()
    {
        return mRsp1aMemorySegment;
    }

    /**
     * Indicates if the Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return Flag.evaluate(sdrplay_api_Rsp1aTunerParamsT.biasTEnable$get(getRsp1aMemorySegment()));
    }

    /**
     * Enable or disable the Bias-T
     */
    public void setBiasT(boolean enable)
    {
        sdrplay_api_Rsp1aTunerParamsT.biasTEnable$set(getRsp1aMemorySegment(), Flag.of(enable));
    }
}
