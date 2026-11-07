package io.github.dsheirer.sdrplay;

/** RSP2 input selection below 60 MHz: {@code sdrplay_api_Rsp2_AmPortSelectT}. */
public enum Rsp2AmPort {
    /** The Hi-Z input. */
    PORT_1(1, "sdrplay_api_Rsp2_AMPORT_1"),
    /** Antenna A or B, the default. */
    PORT_2(0, "sdrplay_api_Rsp2_AMPORT_2");

    private final int code;
    private final String nativeName;

    Rsp2AmPort(int code, String nativeName) {
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
    public static Rsp2AmPort fromCode(int code) {
        for (Rsp2AmPort value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_Rsp2_AmPortSelectT value: " + code);
    }
}
