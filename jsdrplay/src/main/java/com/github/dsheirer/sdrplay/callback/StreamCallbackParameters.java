package com.github.dsheirer.sdrplay.callback;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCbParamsT;
import com.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemorySegment;

/**
 * Stream Callback Parameters structure (sdrplay_api_StreamCbParamsT)
 */
public class StreamCallbackParameters
{
    private int mFirstSampleNumber;
    private boolean mGainReductionChanged;
    private boolean mRfFrequencyChanged;
    private boolean mSampleRateChanged;
    private long mNumberSamples;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public StreamCallbackParameters(MemorySegment memorySegment)
    {
        mFirstSampleNumber = sdrplay_api_StreamCbParamsT.firstSampleNum$get(memorySegment);
        mGainReductionChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.grChanged$get(memorySegment));
        mRfFrequencyChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.rfChanged$get(memorySegment));
        mSampleRateChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.fsChanged$get(memorySegment));
        mNumberSamples = sdrplay_api_StreamCbParamsT.numSamples$get(memorySegment);
    }

    /**
     * First sample number
     */
    public int getFirstSampleNumber()
    {
        return mFirstSampleNumber;
    }

    /**
     * Indicates if gain reduction value has changed
     */
    public boolean isGainReductionChanged()
    {
        return mGainReductionChanged;
    }

    /**
     * Indicates if RF center frequency has changed
     */
    public boolean isRfFrequencyChanged()
    {
        return mRfFrequencyChanged;
    }

    /**
     * Indicates if sample rate has changed
     */
    public boolean isSampleRateChanged()
    {
        return mSampleRateChanged;
    }

    /**
     * Number of samples
     */
    public long getNumberSamples()
    {
        return mNumberSamples;
    }
}
