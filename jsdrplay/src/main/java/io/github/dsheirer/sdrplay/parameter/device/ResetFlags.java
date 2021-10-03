package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_ResetFlagsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Reset Flags structure
 */
public class ResetFlags
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from a foreign memory segment and transfers the structure fields into this instance.
     * @param memorySegment pointer
     */
    public ResetFlags(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
    }

    /**
     * Foreign memory segment
     */
    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Request a reset of the gain reduction
     */
    public void resetGain(boolean reset)
    {
        sdrplay_api_ResetFlagsT.resetGainUpdate$set(getMemorySegment(), reset ? Flag.TRUE : Flag.FALSE);
    }

    /**
     * Request a reset of the center frequency value
     */
    public void resetFrequency(boolean reset)
    {
        sdrplay_api_ResetFlagsT.resetRfUpdate$set(getMemorySegment(), reset ? Flag.TRUE : Flag.FALSE);
    }

    /**
     * Request a reset of the sample rate value.
     */
    public void resetSampleRate(boolean reset)
    {
        sdrplay_api_ResetFlagsT.resetFsUpdate$set(getMemorySegment(), reset ? Flag.TRUE : Flag.FALSE);
    }
}
