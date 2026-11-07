package io.github.dsheirer.sdrplay;

import java.util.EnumSet;
import java.util.Set;

/** Operating modes of the RSPduo: {@code sdrplay_api_RspDuoModeT}. */
public enum RspDuoMode {
    /** Not an RSPduo, or not known. */
    UNKNOWN(0, "sdrplay_api_RspDuoMode_Unknown"),
    /** One tuner, with the full range of sample rates. */
    SINGLE_TUNER(1, "sdrplay_api_RspDuoMode_Single_Tuner"),
    /** Both tuners, streaming coherently at the same sample rate, to one application. */
    DUAL_TUNER(2, "sdrplay_api_RspDuoMode_Dual_Tuner"),
    /**
     * One tuner, while allowing another application, or another device object
     * in this one, to use the other tuner as {@link #SLAVE}. The master sets
     * the sample rate, 6 or 8 MHz, for both.
     */
    MASTER(4, "sdrplay_api_RspDuoMode_Master"),
    /** The tuner the master left free, at the master's sample rate. */
    SLAVE(8, "sdrplay_api_RspDuoMode_Slave");

    private final int code;
    private final String nativeName;

    RspDuoMode(int code, String nativeName) {
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

    /** Returns the mode for an API code, or {@link #UNKNOWN} for an unknown code. */
    public static RspDuoMode fromCode(int code) {
        for (RspDuoMode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        return UNKNOWN;
    }

    /** Returns the modes in a bit mask of modes, as {@code sdrplay_api_GetDevices} reports the available ones. */
    public static Set<RspDuoMode> fromMask(int mask) {
        Set<RspDuoMode> modes = EnumSet.noneOf(RspDuoMode.class);
        for (RspDuoMode mode : values()) {
            if (mode != UNKNOWN && (mask & mode.code) != 0) {
                modes.add(mode);
            }
        }
        return modes;
    }
}
