package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import io.github.dsheirer.sdrplay.api.sdrplay_api_FsFreqT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Sample Rate structure
 */
public class SamplingFrequency
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from a foreign memory segment and transfers the structure fields into this instance.
     * @param memorySegment pointer
     */
    public SamplingFrequency(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
    }

    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Sample rate frequency
     * @return frequency in Hertz
     */
    public double getSampleRate()
    {
        return sdrplay_api_FsFreqT.fsHz$get(getMemorySegment());
    }

    /**
     * Applies the requested sample rate to the device parameters
     * @param sampleRate requested rate
     * @param synchronousUpdate request
     * @param recalibrate request
     */
    public void setSampleRate(SampleRate sampleRate, boolean synchronousUpdate, boolean recalibrate)
    {
        sdrplay_api_FsFreqT.fsHz$set(getMemorySegment(), sampleRate.getSampleRate());
        sdrplay_api_FsFreqT.syncUpdate$set(getMemorySegment(), synchronousUpdate ? Flag.TRUE : Flag.FALSE);
        sdrplay_api_FsFreqT.reCal$set(getMemorySegment(), recalibrate ? Flag.TRUE : Flag.FALSE);
    }

    /**
     * Applies the requested sample rate to the device parameters with synchronous update and recalibrate set to false.
     * @param sampleRate requested rate
     */
    public void setSampleRate(SampleRate sampleRate)
    {
        setSampleRate(sampleRate, false, false);
    }
}
