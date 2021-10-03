package io.github.dsheirer.sdrplay.parameter.event;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * RSP-Duo Mode Callback Event type
 */
public enum RspDuoModeEventType
{
    MASTER_INITIALIZED(sdrplay_api_h.sdrplay_api_MasterInitialised(), "MASTER INITIALIZED"),
    SLAVE_ATTACHED(sdrplay_api_h.sdrplay_api_SlaveAttached(), "SLAVE ATTACHED"),
    SLAVE_DETACHED(sdrplay_api_h.sdrplay_api_SlaveDetached(), "SLAVE DETACHED"),
    SLAVE_INITIALIZED(sdrplay_api_h.sdrplay_api_SlaveInitialised(), "SLAVE INITIALIZED"),
    SLAVE_UNINITIALIZED(sdrplay_api_h.sdrplay_api_SlaveUninitialised(), "SLAVE UNINITIALIZED"),
    MASTER_DLL_DISAPPEARED(sdrplay_api_h.sdrplay_api_MasterDllDisappeared(), "MASTER DLL DISAPPEARED"),
    SLAVE_DLL_DISAPPEARED(sdrplay_api_h.sdrplay_api_SlaveDllDisappeared(), "SLAVE DLL DISAPPEARED"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    RspDuoModeEventType(int value, String description)
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
    public static RspDuoModeEventType fromValue(int value)
    {
        for(RspDuoModeEventType status: RspDuoModeEventType.values())
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
