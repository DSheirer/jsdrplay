package io.github.dsheirer.sdrplay.parameter.control;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * Automatic Gain Control (AGC) Control (mode)
 */
public enum AgcMode
{
    DISABLE(sdrplay_api_h.sdrplay_api_AGC_DISABLE(), "DISABLE"),
    AGC_100_HZ(sdrplay_api_h.sdrplay_api_AGC_100HZ(), "100 Hz"),
    AGC_50_HZ(sdrplay_api_h.sdrplay_api_AGC_50HZ(), "50 Hz"),
    AGC_5_HZ(sdrplay_api_h.sdrplay_api_AGC_5HZ(), "5 Hz"),
    ENABLE(sdrplay_api_h.sdrplay_api_AGC_CTRL_EN(), "ENABLE");

    private int mValue;
    private String mDescription;

    AgcMode(int value, String description)
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
     * @return entry or DISABLE if the code is not recognized
     */
    public static AgcMode fromValue(int value)
    {
        for(AgcMode status: AgcMode.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return DISABLE;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
