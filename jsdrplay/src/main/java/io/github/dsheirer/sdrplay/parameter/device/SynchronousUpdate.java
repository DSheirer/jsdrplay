package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.api.sdrplay_api_SyncUpdateT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Sync Update structure
 */
public class SynchronousUpdate
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from a foreign memory segment
     * @param memorySegment pointer
     */
    public SynchronousUpdate(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
    }

    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Sets the sample number and period to apply to both tuners for a synchronous update.
     * @param sampleNumber value
     * @param period value
     */
    public void set(int sampleNumber, int period)
    {
        sdrplay_api_SyncUpdateT.sampleNum$set(getMemorySegment(), sampleNumber);
        sdrplay_api_SyncUpdateT.period$set(getMemorySegment(), period);
    }
}
