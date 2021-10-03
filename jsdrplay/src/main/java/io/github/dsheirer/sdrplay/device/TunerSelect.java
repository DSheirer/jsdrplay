package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * Tuner Select
 */
public enum TunerSelect
{
    TUNER_1(sdrplay_api_h.sdrplay_api_Tuner_A(), "TUNER 1"),
    TUNER_2(sdrplay_api_h.sdrplay_api_Tuner_B(), "TUNER 2"),
    TUNER_BOTH(sdrplay_api_h.sdrplay_api_Tuner_Both(), "TUNER 1 & 2"),
    NEITHER(sdrplay_api_h.sdrplay_api_Tuner_Neither(), "NEITHER");

    private int mValue;
    private String mDescription;

    TunerSelect(int value, String description)
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
     * @return entry or NEITHER if the code is not recognized
     */
    public static TunerSelect fromValue(int value)
    {
        for(TunerSelect status: TunerSelect.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return NEITHER;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
