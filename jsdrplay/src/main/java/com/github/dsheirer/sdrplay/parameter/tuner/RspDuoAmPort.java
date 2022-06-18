package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * RSP-Duo AM Port
 */
public enum RspDuoAmPort
{
    PORT_1(sdrplay_api_h.sdrplay_api_RspDuo_AMPORT_1(), "PORT 1"),
    PORT_2(sdrplay_api_h.sdrplay_api_RspDuo_AMPORT_2(), "PORT 2"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    RspDuoAmPort(int value, String description)
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
    public static RspDuoAmPort fromValue(int value)
    {
        for(RspDuoAmPort status: RspDuoAmPort.values())
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
