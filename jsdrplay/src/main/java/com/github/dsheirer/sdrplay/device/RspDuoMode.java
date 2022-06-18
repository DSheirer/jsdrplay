package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * RSP-Duo Mode
 */
public enum RspDuoMode
{
    SINGLE_TUNER(sdrplay_api_h.sdrplay_api_RspDuoMode_Single_Tuner(), "SINGLE TUNER"),
    DUAL_TUNER(sdrplay_api_h.sdrplay_api_RspDuoMode_Dual_Tuner(), "DUAL TUNER"),
    MASTER(sdrplay_api_h.sdrplay_api_RspDuoMode_Master(), "MASTER"),
    SLAVE(sdrplay_api_h.sdrplay_api_RspDuoMode_Slave(), "SLAVE"),
    UNKNOWN(sdrplay_api_h.sdrplay_api_RspDuoMode_Unknown(), "UNKNOWN");

    private int mValue;
    private String mDescription;

    RspDuoMode(int value, String description)
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
    public static RspDuoMode fromValue(int value)
    {
        for(RspDuoMode status: RspDuoMode.values())
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
