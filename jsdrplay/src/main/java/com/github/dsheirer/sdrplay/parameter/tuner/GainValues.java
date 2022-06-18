package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_GainValuesT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Gain Values structure (sdrplay_api_GainValuesT)
 *
 * Note: these are output parameters, so there are no setter methods.
 */
public class GainValues
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public GainValues(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }


    public float getCurrent()
    {
        return sdrplay_api_GainValuesT.curr$get(getMemorySegment());
    }

    public float getMax()
    {
        return sdrplay_api_GainValuesT.max$get(getMemorySegment());
    }

    public float getMin()
    {
        return sdrplay_api_GainValuesT.min$get(getMemorySegment());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Current:").append(getCurrent()).append(" Max:").append(getMax()).append(" Min:").append(getMin());
        return sb.toString();
    }
}
