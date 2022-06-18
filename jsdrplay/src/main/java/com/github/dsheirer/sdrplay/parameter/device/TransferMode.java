package com.github.dsheirer.sdrplay.parameter.device;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * USB Transfer Mode
 */
public enum TransferMode
{
    ISOCHRONOUS(sdrplay_api_h.sdrplay_api_ISOCH(), "ISOCHRONOUS"),
    BULK(sdrplay_api_h.sdrplay_api_BULK(), "BULK"),
    UNKNOWN(-1, "UNKNOWN");

    private int mValue;
    private String mDescription;

    TransferMode(int value, String description)
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
    public static TransferMode fromValue(int value)
    {
        for(TransferMode status: TransferMode.values())
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
