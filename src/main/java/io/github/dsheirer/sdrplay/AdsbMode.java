package io.github.dsheirer.sdrplay;

/** Decimation and filtering for ADS-B reception: {@code sdrplay_api_AdsbModeT}. */
public enum AdsbMode {
    /** Normal decimation, the default. */
    DECIMATION(0, "sdrplay_api_ADSB_DECIMATION"),
    /** No decimation, low-pass filter. */
    NO_DECIMATION_LOWPASS(1, "sdrplay_api_ADSB_NO_DECIMATION_LOWPASS"),
    /** No decimation, 2 MHz band-pass filter. */
    NO_DECIMATION_BANDPASS_2MHZ(2, "sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_2MHZ"),
    /** No decimation, 3 MHz band-pass filter. */
    NO_DECIMATION_BANDPASS_3MHZ(3, "sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_3MHZ");

    private final int code;
    private final String nativeName;

    AdsbMode(int code, String nativeName) {
        this.code = code;
        this.nativeName = nativeName;
    }

    /** Returns the value the API uses. */
    public int code() {
        return code;
    }

    /** Returns the C name of the value. */
    public String nativeName() {
        return nativeName;
    }

    /**
     * Returns the value for an API code.
     *
     * @throws IllegalArgumentException if the code is unknown
     */
    public static AdsbMode fromCode(int code) {
        for (AdsbMode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_AdsbModeT value: " + code);
    }
}
