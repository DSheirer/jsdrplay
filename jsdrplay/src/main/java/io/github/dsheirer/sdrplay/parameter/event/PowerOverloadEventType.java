package io.github.dsheirer.sdrplay.parameter.event;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * Power Overload Callback Event type
 */
public enum PowerOverloadEventType
{
    OVERLOAD_DETECTED(sdrplay_api_h.sdrplay_api_Overload_Detected(), "OVERLOAD DETECTED"),
    OVERLOAD_CORRECTED(sdrplay_api_h.sdrplay_api_Overload_Corrected(), "OVERLOAD CORRECTED"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    PowerOverloadEventType(int value, String description)
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
    public static PowerOverloadEventType fromValue(int value)
    {
        for(PowerOverloadEventType status: PowerOverloadEventType.values())
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
