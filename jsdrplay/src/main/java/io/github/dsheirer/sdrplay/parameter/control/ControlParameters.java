package io.github.dsheirer.sdrplay.parameter.control;

import io.github.dsheirer.sdrplay.api.sdrplay_api_ControlParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Control Parameters structure (sdrplay_api_ControlParamsT)
 */
public class ControlParameters
{
    private MemorySegment mMemorySegment;
    private DcOffset mDcOffset;
    private Decimation mDecimation;
    private Agc mAgc;

    /**
     * Creates an instance from the foreign memory segment
     */
    public ControlParameters(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
        mDcOffset = new DcOffset(sdrplay_api_ControlParamsT.dcOffset$slice(memorySegment));
        mDecimation = new Decimation(sdrplay_api_ControlParamsT.decimation$slice(memorySegment));
        mAgc = new Agc(sdrplay_api_ControlParamsT.agc$slice(memorySegment));
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * DC offset settings for DC and IQ correction
     */
    public DcOffset getDcOffset()
    {
        return mDcOffset;
    }

    /**
     * Decimation settings
     */
    public Decimation getDecimation()
    {
        return mDecimation;
    }

    /**
     * Automatic Gain Control (AGC) settings
     */
    public Agc getAgc()
    {
        return mAgc;
    }

    /**
     * Current ADSB mode
     */
    public AdsbMode getAdsbMode()
    {
        return AdsbMode.fromValue(sdrplay_api_ControlParamsT.adsbMode$get(getMemorySegment()));
    }

    /**
     * Sets ADSB mode
     */
    public void setAdsbMode(AdsbMode mode)
    {
        sdrplay_api_ControlParamsT.adsbMode$set(getMemorySegment(), mode.getValue());
    }
}
