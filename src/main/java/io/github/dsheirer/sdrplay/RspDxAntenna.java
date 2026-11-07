package io.github.dsheirer.sdrplay;

/** RSPdx and RSPdx-R2 antenna input: {@code sdrplay_api_RspDx_AntennaSelectT}. */
public enum RspDxAntenna {
    /** Antenna A, the default. */
    A(0, "sdrplay_api_RspDx_ANTENNA_A"),
    /** Antenna B. */
    B(1, "sdrplay_api_RspDx_ANTENNA_B"),
    /** Antenna C. */
    C(2, "sdrplay_api_RspDx_ANTENNA_C");

    private final int code;
    private final String nativeName;

    RspDxAntenna(int code, String nativeName) {
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
    public static RspDxAntenna fromCode(int code) {
        for (RspDxAntenna value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_RspDx_AntennaSelectT value: " + code);
    }
}
