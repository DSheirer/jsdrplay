package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * RSP-Dx Antenna
 */
public enum RspDxAntenna
{
    ANTENNA_A(sdrplay_api_h.sdrplay_api_RspDx_ANTENNA_A(), "ANTENNA A"),
    ANTENNA_B(sdrplay_api_h.sdrplay_api_RspDx_ANTENNA_B(), "ANTENNA B"),
    ANTENNA_C(sdrplay_api_h.sdrplay_api_RspDx_ANTENNA_C(), "ANTENNA C"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    RspDxAntenna(int value, String description)
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
    public static RspDxAntenna fromValue(int value)
    {
        for(RspDxAntenna status: RspDxAntenna.values())
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
