package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.api.sdrplay_api_StreamCbParamsT;
import io.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemorySegment;

/**
 * Stream Callback Parameters structure (sdrplay_api_StreamCbParamsT)
 */
public class StreamCallbackParameters
{
    private int mFirstSampleNumber;
    private boolean mGrChanged;
    private boolean mRfChanged;
    private boolean mFsChanged;
    private long mNumberSamples;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public StreamCallbackParameters(MemorySegment memorySegment)
    {
        mFirstSampleNumber = sdrplay_api_StreamCbParamsT.firstSampleNum$get(memorySegment);
        mGrChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.grChanged$get(memorySegment));
        mRfChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.rfChanged$get(memorySegment));
        mFsChanged = Flag.evaluate(sdrplay_api_StreamCbParamsT.fsChanged$get(memorySegment));
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
    public boolean isGrChanged()
    {
        return mGrChanged;
    }

    /**
     * Indicates if RF center frequency has changed
     */
    public boolean isRfChanged()
    {
        return mRfChanged;
    }

    /**
     * Indicates if sample rate has changed
     */
    public boolean isFsChanged()
    {
        return mFsChanged;
    }

    /**
     * Number of samples
     */
    public long getNumberSamples()
    {
        return mNumberSamples;
    }
}
