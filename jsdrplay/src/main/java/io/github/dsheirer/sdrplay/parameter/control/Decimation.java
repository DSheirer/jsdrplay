package io.github.dsheirer.sdrplay.parameter.control;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DecimationT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Decimation structure (sdrplay_api_DecimationT)
 */
public class Decimation
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public Decimation(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    public MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Indicates if decimation is enabled
     */
    public boolean isEnabled()
    {
        return Flag.evaluate(sdrplay_api_DecimationT.enable$get(getMemorySegment()));
    }

    /**
     * Enable or disable decimation
     */
    public void setEnabled(boolean enable)
    {
        sdrplay_api_DecimationT.enable$set(getMemorySegment(), Flag.of(enable));
    }

    /**
     * Decimation factor
     */
    public int getDecimationFactor()
    {
        return sdrplay_api_DecimationT.decimationFactor$get(getMemorySegment());
    }

    /**
     * Sets the decimation factor
     */
    public void setDecimationFactor(int decimationFactor)
    {
        sdrplay_api_DecimationT.decimationFactor$set(getMemorySegment(), (byte)decimationFactor);
    }

    /**
     * Indicates if the wideband signal setting is enabled
     */
    public boolean isWideBandSignal()
    {
        return Flag.evaluate(sdrplay_api_DecimationT.wideBandSignal$get(getMemorySegment()));
    }

    /**
     * Enables or disables the wideband signal setting
     */
    public void setWideBandSignal(boolean enable)
    {
        sdrplay_api_DecimationT.wideBandSignal$set(getMemorySegment(), Flag.of(enable));
    }
}
