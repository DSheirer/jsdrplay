package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * RSP-2 Antenna
 */
public enum Rsp2Antenna
{
    ANTENNA_A(sdrplay_api_h.sdrplay_api_Rsp2_ANTENNA_A(), "ANTENNA A"),
    ANTENNA_B(sdrplay_api_h.sdrplay_api_Rsp2_ANTENNA_B(), "ANTENNA B"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    Rsp2Antenna(int value, String description)
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
     * @return entry or UKNOWN if the code is not recognized
     */
    public static Rsp2Antenna fromValue(int value)
    {
        for(Rsp2Antenna status: Rsp2Antenna.values())
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
