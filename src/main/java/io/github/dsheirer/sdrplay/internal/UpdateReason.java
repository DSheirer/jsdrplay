package io.github.dsheirer.sdrplay.internal;

/**
 * Reasons passed to {@code sdrplay_api_Update}: the values of
 * {@code sdrplay_api_ReasonForUpdateT} and, for those marked as extension 1,
 * {@code sdrplay_api_ReasonForUpdateExtension1T}.
 */
public enum UpdateReason {
    NONE(0x00000000, "sdrplay_api_Update_None", false),
    DEV_FS(0x00000001, "sdrplay_api_Update_Dev_Fs", false),
    DEV_PPM(0x00000002, "sdrplay_api_Update_Dev_Ppm", false),
    DEV_SYNC_UPDATE(0x00000004, "sdrplay_api_Update_Dev_SyncUpdate", false),
    DEV_RESET_FLAGS(0x00000008, "sdrplay_api_Update_Dev_ResetFlags", false),
    RSP1A_BIAS_T_CONTROL(0x00000010, "sdrplay_api_Update_Rsp1a_BiasTControl", false),
    RSP1A_RF_NOTCH_CONTROL(0x00000020, "sdrplay_api_Update_Rsp1a_RfNotchControl", false),
    RSP1A_RF_DAB_NOTCH_CONTROL(0x00000040, "sdrplay_api_Update_Rsp1a_RfDabNotchControl", false),
    RSP2_BIAS_T_CONTROL(0x00000080, "sdrplay_api_Update_Rsp2_BiasTControl", false),
    RSP2_AM_PORT_SELECT(0x00000100, "sdrplay_api_Update_Rsp2_AmPortSelect", false),
    RSP2_ANTENNA_CONTROL(0x00000200, "sdrplay_api_Update_Rsp2_AntennaControl", false),
    RSP2_RF_NOTCH_CONTROL(0x00000400, "sdrplay_api_Update_Rsp2_RfNotchControl", false),
    RSP2_EXT_REF_CONTROL(0x00000800, "sdrplay_api_Update_Rsp2_ExtRefControl", false),
    RSPDUO_EXT_REF_CONTROL(0x00001000, "sdrplay_api_Update_RspDuo_ExtRefControl", false),
    TUNER_GR(0x00008000, "sdrplay_api_Update_Tuner_Gr", false),
    TUNER_GR_LIMITS(0x00010000, "sdrplay_api_Update_Tuner_GrLimits", false),
    TUNER_FRF(0x00020000, "sdrplay_api_Update_Tuner_Frf", false),
    TUNER_BW_TYPE(0x00040000, "sdrplay_api_Update_Tuner_BwType", false),
    TUNER_IF_TYPE(0x00080000, "sdrplay_api_Update_Tuner_IfType", false),
    TUNER_DC_OFFSET(0x00100000, "sdrplay_api_Update_Tuner_DcOffset", false),
    TUNER_LO_MODE(0x00200000, "sdrplay_api_Update_Tuner_LoMode", false),
    CTRL_DC_OFFSET_IQ_IMBALANCE(0x00400000, "sdrplay_api_Update_Ctrl_DCoffsetIQimbalance", false),
    CTRL_DECIMATION(0x00800000, "sdrplay_api_Update_Ctrl_Decimation", false),
    CTRL_AGC(0x01000000, "sdrplay_api_Update_Ctrl_Agc", false),
    CTRL_ADSB_MODE(0x02000000, "sdrplay_api_Update_Ctrl_AdsbMode", false),
    CTRL_OVERLOAD_MSG_ACK(0x04000000, "sdrplay_api_Update_Ctrl_OverloadMsgAck", false),
    RSPDUO_BIAS_T_CONTROL(0x08000000, "sdrplay_api_Update_RspDuo_BiasTControl", false),
    RSPDUO_AM_PORT_SELECT(0x10000000, "sdrplay_api_Update_RspDuo_AmPortSelect", false),
    RSPDUO_TUNER1_AM_NOTCH_CONTROL(0x20000000, "sdrplay_api_Update_RspDuo_Tuner1AmNotchControl", false),
    RSPDUO_RF_NOTCH_CONTROL(0x40000000, "sdrplay_api_Update_RspDuo_RfNotchControl", false),
    RSPDUO_RF_DAB_NOTCH_CONTROL(0x80000000, "sdrplay_api_Update_RspDuo_RfDabNotchControl", false),

    EXT1_NONE(0x00000000, "sdrplay_api_Update_Ext1_None", true),
    RSPDX_HDR_ENABLE(0x00000001, "sdrplay_api_Update_RspDx_HdrEnable", true),
    RSPDX_BIAS_T_CONTROL(0x00000002, "sdrplay_api_Update_RspDx_BiasTControl", true),
    RSPDX_ANTENNA_CONTROL(0x00000004, "sdrplay_api_Update_RspDx_AntennaControl", true),
    RSPDX_RF_NOTCH_CONTROL(0x00000008, "sdrplay_api_Update_RspDx_RfNotchControl", true),
    RSPDX_RF_DAB_NOTCH_CONTROL(0x00000010, "sdrplay_api_Update_RspDx_RfDabNotchControl", true),
    RSPDX_HDR_BW(0x00000020, "sdrplay_api_Update_RspDx_HdrBw", true),
    RSPDUO_RESET_SLAVE_FLAGS(0x00000040, "sdrplay_api_Update_RspDuo_ResetSlaveFlags", true);

    private final int code;
    private final String nativeName;
    private final boolean extension1;

    UpdateReason(int code, String nativeName, boolean extension1) {
        this.code = code;
        this.nativeName = nativeName;
        this.extension1 = extension1;
    }

    /** Returns the flag value. */
    public int code() {
        return code;
    }

    /** Returns the C name of the flag. */
    public String nativeName() {
        return nativeName;
    }

    /** Returns true for a flag of {@code sdrplay_api_ReasonForUpdateExtension1T}. */
    public boolean extension1() {
        return extension1;
    }

    /** Returns the {@code reasonForUpdate} argument of {@code sdrplay_api_Update}. */
    public int reason() {
        return extension1 ? 0 : code;
    }

    /** Returns the {@code reasonForUpdateExt1} argument of {@code sdrplay_api_Update}. */
    public int reasonExtension1() {
        return extension1 ? code : 0;
    }
}
