package io.github.dsheirer.sdrplay.parameter.control;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DcOffsetT;
import jdk.incubator.foreign.MemorySegment;

/**
 * DC Offset structure (sdrplay_api_DCOffsetT)
 */
public class DcOffset
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public DcOffset(MemorySegment memorySegment)
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

    /**
     * Indicates if DC correction is enabled.
     */
    public boolean isDC()
    {
        return Flag.evaluate(sdrplay_api_DcOffsetT.DCenable$get(getMemorySegment()));
    }

    /**
     * Enable or disable DC correction
     */
    public void setDC(boolean enable)
    {
        sdrplay_api_DcOffsetT.DCenable$set(getMemorySegment(), Flag.of(enable));
    }

    /**
     * Indicates if IQ correction is enabled
     */
    public boolean isIQ()
    {
        return Flag.evaluate(sdrplay_api_DcOffsetT.IQenable$get(getMemorySegment()));
    }

    /**
     * Enable or disable IQ correction
     */
    public void setIQ(boolean enable)
    {
        sdrplay_api_DcOffsetT.IQenable$set(getMemorySegment(), Flag.of(enable));
    }
}
