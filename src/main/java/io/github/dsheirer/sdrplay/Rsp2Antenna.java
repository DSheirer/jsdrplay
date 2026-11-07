package io.github.dsheirer.sdrplay;

/** RSP2 antenna input: {@code sdrplay_api_Rsp2_AntennaSelectT}. */
public enum Rsp2Antenna {
    /** Antenna A, the default. */
    A(5, "sdrplay_api_Rsp2_ANTENNA_A"),
    /** Antenna B. */
    B(6, "sdrplay_api_Rsp2_ANTENNA_B");

    private final int code;
    private final String nativeName;

    Rsp2Antenna(int code, String nativeName) {
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
    public static Rsp2Antenna fromCode(int code) {
        for (Rsp2Antenna value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_Rsp2_AntennaSelectT value: " + code);
    }
}
