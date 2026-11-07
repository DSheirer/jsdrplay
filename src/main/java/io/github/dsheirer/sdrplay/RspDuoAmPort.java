package io.github.dsheirer.sdrplay;

/** RSPduo tuner 1 input selection below 60 MHz: {@code sdrplay_api_RspDuo_AmPortSelectT}. */
public enum RspDuoAmPort {
    /** The Hi-Z input. */
    PORT_1(1, "sdrplay_api_RspDuo_AMPORT_1"),
    /** The 50 ohm tuner 1 input, the default. */
    PORT_2(0, "sdrplay_api_RspDuo_AMPORT_2");

    private final int code;
    private final String nativeName;

    RspDuoAmPort(int code, String nativeName) {
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
    public static RspDuoAmPort fromCode(int code) {
        for (RspDuoAmPort value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_RspDuo_AmPortSelectT value: " + code);
    }
}
