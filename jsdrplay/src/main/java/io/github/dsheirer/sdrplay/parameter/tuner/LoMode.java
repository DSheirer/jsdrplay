package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.api.sdrplay_api_h;

/**
 * Local Oscillator Mode
 */
public enum LoMode
{
    AUTO(sdrplay_api_h.sdrplay_api_LO_Auto(), "AUTO"),
    MHZ_120(sdrplay_api_h.sdrplay_api_LO_120MHz(), "120 MHz"),
    MHZ_144(sdrplay_api_h.sdrplay_api_LO_144MHz(), "144 MHz"),
    MHZ_168(sdrplay_api_h.sdrplay_api_LO_168MHz(), "168 MHz"),
    UNDEFINED(sdrplay_api_h.sdrplay_api_LO_Undefined(), "UNDEFINED");

    private int mValue;
    private String mDescription;

    LoMode(int value, String description)
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
    public static LoMode fromValue(int value)
    {
        for(LoMode status: LoMode.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return UNDEFINED;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
