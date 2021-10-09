package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * Intermediate Frequency (IF) Mode / Frequency
 */
public enum IfMode
{
    IF_ZERO(sdrplay_api_h.sdrplay_api_IF_Zero(), "0.000 MHz"),
    IF_450(sdrplay_api_h.sdrplay_api_IF_0_450(), "0.450 MHz"),
    IF_1620(sdrplay_api_h.sdrplay_api_IF_1_620(), "1.620 MHz"),
    IF_2048(sdrplay_api_h.sdrplay_api_IF_2_048(), "2.048 MHz"),
    UNDEFINED(sdrplay_api_h.sdrplay_api_IF_Undefined(), "UNDEFINED");

    private int mValue;
    private String mDescription;

    IfMode(int value, String description)
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
    public static IfMode fromValue(int value)
    {
        for(IfMode status: IfMode.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return UNDEFINED;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
