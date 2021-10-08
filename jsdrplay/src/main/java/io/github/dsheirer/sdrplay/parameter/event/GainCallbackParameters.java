package io.github.dsheirer.sdrplay.parameter.event;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_GainCbParamT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Gain Callback Parameters structure (sdrplay_api_GainCbParamT)
 */
public class GainCallbackParameters
{
    private int mGainReductionDb;
    private int mLnaGainReductionDb;
    private double mCurrentGain;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public GainCallbackParameters(MemorySegment memorySegment)
    {
        mGainReductionDb = sdrplay_api_GainCbParamT.gRdB$get(memorySegment);
        mLnaGainReductionDb = sdrplay_api_GainCbParamT.lnaGRdB$get(memorySegment);
        mCurrentGain = sdrplay_api_GainCbParamT.currGain$get(memorySegment);
    }

    /**
     * Current gain reduction value
     */
    public int getGainReductionDb()
    {
        return mGainReductionDb;
    }

    /**
     * Current LNA state setting
     */
    public int getLnaGainReductionDb()
    {
        return mLnaGainReductionDb;
    }

    /**
     * Current gain value
     */
    public double getCurrentGain()
    {
        return mCurrentGain;
    }
}
