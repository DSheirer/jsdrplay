package io.github.dsheirer.sdrplay;

import java.util.EnumSet;
import java.util.Set;

/**
 * Tuner selection: {@code sdrplay_api_TunerSelectT}. Only the RSPduo has a
 * second tuner; every other model uses {@link #A}.
 */
public enum Tuner {
    /** No tuner. */
    NEITHER(0, "sdrplay_api_Tuner_Neither"),
    /** Tuner A, called tuner 1 on the RSPduo. */
    A(1, "sdrplay_api_Tuner_A"),
    /** Tuner B, called tuner 2 on the RSPduo. */
    B(2, "sdrplay_api_Tuner_B"),
    /** Both RSPduo tuners, in dual tuner mode. */
    BOTH(3, "sdrplay_api_Tuner_Both");

    private final int code;
    private final String nativeName;

    Tuner(int code, String nativeName) {
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

    /** Returns the tuner for an API code, or {@link #NEITHER} for an unknown code. */
    public static Tuner fromCode(int code) {
        for (Tuner tuner : values()) {
            if (tuner.code == code) {
                return tuner;
            }
        }
        return NEITHER;
    }

    /** Returns the single tuners, {@link #A} and {@link #B}, in a bit mask of tuners. */
    public static Set<Tuner> fromMask(int mask) {
        Set<Tuner> tuners = EnumSet.noneOf(Tuner.class);
        if ((mask & A.code) != 0) {
            tuners.add(A);
        }
        if ((mask & B.code) != 0) {
            tuners.add(B);
        }
        return tuners;
    }
}
