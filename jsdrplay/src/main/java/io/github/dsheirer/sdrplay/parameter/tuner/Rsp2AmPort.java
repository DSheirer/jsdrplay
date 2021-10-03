package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * RSP-2 AM Port
 */
public enum Rsp2AmPort
{
    PORT_1(sdrplay_api_h.sdrplay_api_Rsp2_AMPORT_1(), "PORT 1"),
    PORT_2(sdrplay_api_h.sdrplay_api_Rsp2_AMPORT_2(), "PORT 2"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    Rsp2AmPort(int value, String description)
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
    public static Rsp2AmPort fromValue(int value)
    {
        for(Rsp2AmPort status: Rsp2AmPort.values())
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
