package io.github.dsheirer.sdrplay;

/** SDRplay receiver models, identified by the hardware version the API reports. */
public enum RspModel {
    /** RSP1. */
    RSP1(1, "SDRPLAY_RSP1_ID", "RSP1"),
    /** RSP1A. */
    RSP1A(255, "SDRPLAY_RSP1A_ID", "RSP1A"),
    /** RSP1B. */
    RSP1B(6, "SDRPLAY_RSP1B_ID", "RSP1B"),
    /** RSP2 and RSP2pro. */
    RSP2(2, "SDRPLAY_RSP2_ID", "RSP2"),
    /** RSPduo, with two tuners. */
    RSPDUO(3, "SDRPLAY_RSPduo_ID", "RSPduo"),
    /** RSPdx. */
    RSPDX(4, "SDRPLAY_RSPdx_ID", "RSPdx"),
    /** RSPdx-R2. */
    RSPDX_R2(7, "SDRPLAY_RSPdxR2_ID", "RSPdx-R2"),
    /** A hardware version this version of jsdrplay does not know. */
    UNKNOWN(-1, null, "Unknown RSP");

    private final int code;
    private final String nativeName;
    private final String displayName;

    RspModel(int code, String nativeName, String displayName) {
        this.code = code;
        this.nativeName = nativeName;
        this.displayName = displayName;
    }

    /** Returns the hardware version, the {@code hwVer} field of {@code sdrplay_api_DeviceT}. */
    public int code() {
        return code;
    }

    /** Returns the name of the C constant, such as {@code SDRPLAY_RSP1A_ID}, or null for {@link #UNKNOWN}. */
    public String nativeName() {
        return nativeName;
    }

    /** Returns SDRplay's name for the model, such as {@code RSPdx-R2}. */
    public String displayName() {
        return displayName;
    }

    /** Returns the model for a hardware version, or {@link #UNKNOWN}. */
    public static RspModel fromCode(int code) {
        for (RspModel model : values()) {
            if (model.code == code) {
                return model;
            }
        }
        return UNKNOWN;
    }
}
