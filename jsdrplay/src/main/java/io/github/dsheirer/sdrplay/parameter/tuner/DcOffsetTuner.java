package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DcOffsetTunerT;
import jdk.incubator.foreign.MemorySegment;

/**
 * DC Offset Tuner structure (sdrplay_api_DcOffsetTunerT)
 */
public class DcOffsetTuner
{
    private MemorySegment mMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public DcOffsetTuner(MemorySegment memorySegment)
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
     * DC calibration mode.
     * @return
     */
    public int getDcCal()
    {
        return sdrplay_api_DcOffsetTunerT.dcCal$get(getMemorySegment());
    }

    public void setDcCal(int dcCal)
    {
        sdrplay_api_DcOffsetTunerT.dcCal$set(getMemorySegment(), (byte)dcCal);
    }

    public boolean isSpeedUp()
    {
        return Flag.evaluate(sdrplay_api_DcOffsetTunerT.speedUp$get(getMemorySegment()));
    }

    public void setSpeedUp(boolean speedUp)
    {
        sdrplay_api_DcOffsetTunerT.speedUp$set(getMemorySegment(), Flag.of(speedUp));
    }

    public int getTrackTime()
    {
        return sdrplay_api_DcOffsetTunerT.trackTime$get(getMemorySegment());
    }

    public void setTrackTime(int trackTime)
    {
        sdrplay_api_DcOffsetTunerT.trackTime$set(getMemorySegment(), trackTime);
    }

    public int getRefreshRateTime()
    {
        return sdrplay_api_DcOffsetTunerT.refreshRateTime$get(getMemorySegment());
    }

    public void setRefreshRateTime(int refreshRateTime)
    {
        sdrplay_api_DcOffsetTunerT.refreshRateTime$set(getMemorySegment(), refreshRateTime);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DC Cal:").append(getDcCal()).append(" Speed Up:").append(isSpeedUp());
        sb.append(" Track Time:").append(getTrackTime()).append(" Refresh Rate Time:").append(getRefreshRateTime());
        return sb.toString();
    }
}
