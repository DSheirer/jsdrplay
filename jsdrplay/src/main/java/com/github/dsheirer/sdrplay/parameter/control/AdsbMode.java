package com.github.dsheirer.sdrplay.parameter.control;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;

/**
 * ADSB Mode (for ADS-B decoding applications)
 */
public enum AdsbMode
{
    NO_DECIMATION_LOWPASS(sdrplay_api_h.sdrplay_api_ADSB_NO_DECIMATION_LOWPASS(), "No Decimation-Lowpass"),
    NO_DECIMATION_BANDPASS_2_MHZ(sdrplay_api_h.sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_2MHZ(), "No Decimation-Bandpass 2 MHz"),
    NO_DECIMATION_BANDPASS_3_MHZ(sdrplay_api_h.sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_3MHZ(), "No Decimation-Bandpass 3 MHz"),
    DECIMATION(sdrplay_api_h.sdrplay_api_ADSB_DECIMATION(), "Decimation");

    private int mValue;
    private String mDescription;

    AdsbMode(int value, String description)
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
     * @return entry or DECIMATION if the code is not recognized
     */
    public static AdsbMode fromValue(int value)
    {
        for(AdsbMode status: AdsbMode.values())
        {
            if(status.getValue() == value)
            {
                return status;
            }
        }
        
        return DECIMATION;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
