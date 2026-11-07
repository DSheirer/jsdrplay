package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.util.Objects;

/** An RSPdx or RSPdx-R2. */
public final class RspDxDevice extends RspDevice {
    private static final long HDR = Layouts.offset(Layouts.DEV_PARAMS, "rspDxParams", "hdrEnable");
    private static final long BIAS_T = Layouts.offset(Layouts.DEV_PARAMS, "rspDxParams", "biasTEnable");
    private static final long ANTENNA = Layouts.offset(Layouts.DEV_PARAMS, "rspDxParams", "antennaSel");
    private static final long RF_NOTCH = Layouts.offset(Layouts.DEV_PARAMS, "rspDxParams", "rfNotchEnable");
    private static final long RF_DAB_NOTCH = Layouts.offset(Layouts.DEV_PARAMS, "rspDxParams", "rfDabNotchEnable");

    RspDxDevice(Selection selection) {
        super(selection);
    }

    /** Returns true when HDR (high dynamic range) mode is on. */
    public boolean isHdr() {
        return devParams().get(JAVA_BYTE, HDR) != 0;
    }

    /**
     * Turns HDR (high dynamic range) mode on or off. HDR mode works only at
     * particular frequencies below 2 MHz; see the SDRplay API specification.
     */
    public void setHdr(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSPDX_HDR_ENABLE, () -> devParams().set(JAVA_BYTE, HDR, RxChannel.flag(enabled)));
    }

    /** Returns the bandwidth used in HDR mode. */
    public RspDxHdrBandwidth hdrBandwidth() {
        return RspDxHdrBandwidth.fromCode(channel().params().get(JAVA_INT, RxChannel.Offsets.RSPDX_HDR_BW));
    }

    /** Sets the bandwidth used in HDR mode. */
    public void setHdrBandwidth(RspDxHdrBandwidth bandwidth) throws SDRplayException {
        Objects.requireNonNull(bandwidth, "bandwidth");
        updateChannel(channel(), UpdateReason.RSPDX_HDR_BW,
            () -> channel().params().set(JAVA_INT, RxChannel.Offsets.RSPDX_HDR_BW, bandwidth.code()));
    }

    /** Returns true when the bias tee is on. */
    public boolean isBiasT() {
        return devParams().get(JAVA_BYTE, BIAS_T) != 0;
    }

    /**
     * Turns the bias tee on or off. This puts DC power on the antenna
     * connector, which can damage equipment not built for it.
     */
    public void setBiasT(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSPDX_BIAS_T_CONTROL,
            () -> devParams().set(JAVA_BYTE, BIAS_T, RxChannel.flag(enabled)));
    }

    /** Returns the selected antenna input. */
    public RspDxAntenna antenna() {
        return RspDxAntenna.fromCode(devParams().get(JAVA_INT, ANTENNA));
    }

    /** Selects antenna input A, B or C. */
    public void setAntenna(RspDxAntenna antenna) throws SDRplayException {
        Objects.requireNonNull(antenna, "antenna");
        updateDevice(UpdateReason.RSPDX_ANTENNA_CONTROL, () -> devParams().set(JAVA_INT, ANTENNA, antenna.code()));
    }

    /** Returns true when the broadcast FM notch filter is on. */
    public boolean isRfNotch() {
        return devParams().get(JAVA_BYTE, RF_NOTCH) != 0;
    }

    /** Turns the broadcast FM notch filter on or off. */
    public void setRfNotch(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSPDX_RF_NOTCH_CONTROL,
            () -> devParams().set(JAVA_BYTE, RF_NOTCH, RxChannel.flag(enabled)));
    }

    /** Returns true when the DAB notch filter is on. */
    public boolean isRfDabNotch() {
        return devParams().get(JAVA_BYTE, RF_DAB_NOTCH) != 0;
    }

    /** Turns the DAB notch filter on or off. */
    public void setRfDabNotch(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSPDX_RF_DAB_NOTCH_CONTROL,
            () -> devParams().set(JAVA_BYTE, RF_DAB_NOTCH, RxChannel.flag(enabled)));
    }
}
