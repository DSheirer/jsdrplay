package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * Minimum Gain Reduction (mode)
 */
public enum MinimumGainReductionMode
{
    EXTENDED(sdrplay_api_h.sdrplay_api_EXTENDED_MIN_GR(), "EXTENDED"),
    NORMAL(sdrplay_api_h.sdrplay_api_NORMAL_MIN_GR(), "NORMAL"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    MinimumGainReductionMode(int value, String description)
    {
        mValue = value;
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
     * Lookup the entry from a return code
     * @param value to lookup
     * @return entry or UNKNOWN if the code is not recognized
     */
    public static MinimumGainReductionMode fromValue(int value)
    {
        for(MinimumGainReductionMode status: MinimumGainReductionMode.values())
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
