package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * RSPdx High Dynamic Range (HDR) Mode Bandwidth
 */
public enum HdrModeBandwidth
{
    BANDWIDTH_0_200(sdrplay_api_h.sdrplay_api_RspDx_HDRMODE_BW_0_200(), 200_000, ".2 MHz"),
    BANDWIDTH_0_500(sdrplay_api_h.sdrplay_api_RspDx_HDRMODE_BW_0_500(), 500_000, ".5 MHz"),
    BANDWIDTH_1_200(sdrplay_api_h.sdrplay_api_RspDx_HDRMODE_BW_1_200(), 1_200_000, "1.2 MHz"),
    BANDWIDTH_1_700(sdrplay_api_h.sdrplay_api_RspDx_HDRMODE_BW_1_700(), 1_700_000, "1.7 MHz"),
    UNKNOWN(-1, 0, "UNKNOWN");

    private int mValue;
    private long mBandwidth;
    private String mDescription;

    HdrModeBandwidth(int value, long bandwidth, String description)
    {
        mValue = value;
        mBandwidth = bandwidth;
        mDescription = description;
    }

    /**
     * Numeric value
     */
    public int getValue()
    {
        return mValue;
    }

    /**
     * Bandwidth in Hertz
     */
    public long getBandwidth()
    {
        return mBandwidth;
    }

    /**
     * Lookup the entry from a return code
     * @param value to lookup
     * @return entry or UKNOWN if the code is not recognized
     */
    public static HdrModeBandwidth fromValue(int value)
    {
        for(HdrModeBandwidth status: HdrModeBandwidth.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return UNKNOWN;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
