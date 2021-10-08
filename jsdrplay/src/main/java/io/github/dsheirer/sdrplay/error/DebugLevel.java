package io.github.dsheirer.sdrplay.error;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * Debug Level
 */
public enum DebugLevel
{
    DISABLE(sdrplay_api_h.sdrplay_api_DbgLvl_Disable(), "DISABLE"),
    VERBOSE(sdrplay_api_h.sdrplay_api_DbgLvl_Verbose(), "VERBOSE"),
    WARNING(sdrplay_api_h.sdrplay_api_DbgLvl_Warning(), "WARNING"),
    ERROR(sdrplay_api_h.sdrplay_api_DbgLvl_Error(), "ERROR"),
    MESSAGE(sdrplay_api_h.sdrplay_api_DbgLvl_Message(), "MESSAGE"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    DebugLevel(int value, String description)
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
    public static DebugLevel fromValue(int value)
    {
        for(DebugLevel status: DebugLevel.values())
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
