package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.util.Objects;

/** An RSP2 or RSP2pro. */
public final class Rsp2Device extends RspDevice {
    private static final long EXT_REF = Layouts.offset(Layouts.DEV_PARAMS, "rsp2Params", "extRefOutputEn");

    Rsp2Device(Selection selection) {
        super(selection);
    }

    /** Returns true when the reference clock output is on. */
    public boolean isExternalReferenceOutput() {
        return devParams().get(JAVA_BYTE, EXT_REF) != 0;
    }

    /** Turns the reference clock output on or off. */
    public void setExternalReferenceOutput(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSP2_EXT_REF_CONTROL,
            () -> devParams().set(JAVA_BYTE, EXT_REF, RxChannel.flag(enabled)));
    }

    /** Returns true when the bias tee is on. */
    public boolean isBiasT() {
        return channel().params().get(JAVA_BYTE, RxChannel.Offsets.RSP2_BIAS_T) != 0;
    }

    /**
     * Turns the bias tee on or off. This puts DC power on the antenna
     * connector, which can damage equipment not built for it.
     */
    public void setBiasT(boolean enabled) throws SDRplayException {
        updateChannel(channel(), UpdateReason.RSP2_BIAS_T_CONTROL,
            () -> channel().params().set(JAVA_BYTE, RxChannel.Offsets.RSP2_BIAS_T, RxChannel.flag(enabled)));
    }

    /** Returns the input used below 60 MHz. */
    public Rsp2AmPort amPort() {
        return Rsp2AmPort.fromCode(channel().params().get(JAVA_INT, RxChannel.Offsets.RSP2_AM_PORT));
    }

    /** Selects the input used below 60 MHz. */
    public void setAmPort(Rsp2AmPort port) throws SDRplayException {
        Objects.requireNonNull(port, "port");
        updateChannel(channel(), UpdateReason.RSP2_AM_PORT_SELECT,
            () -> channel().params().set(JAVA_INT, RxChannel.Offsets.RSP2_AM_PORT, port.code()));
    }

    /** Returns the selected antenna input. */
    public Rsp2Antenna antenna() {
        return Rsp2Antenna.fromCode(channel().params().get(JAVA_INT, RxChannel.Offsets.RSP2_ANTENNA));
    }

    /** Selects antenna input A or B. */
    public void setAntenna(Rsp2Antenna antenna) throws SDRplayException {
        Objects.requireNonNull(antenna, "antenna");
        updateChannel(channel(), UpdateReason.RSP2_ANTENNA_CONTROL,
            () -> channel().params().set(JAVA_INT, RxChannel.Offsets.RSP2_ANTENNA, antenna.code()));
    }

    /** Returns true when the broadcast FM notch filter is on. */
    public boolean isRfNotch() {
        return channel().params().get(JAVA_BYTE, RxChannel.Offsets.RSP2_RF_NOTCH) != 0;
    }

    /** Turns the broadcast FM notch filter on or off. */
    public void setRfNotch(boolean enabled) throws SDRplayException {
        updateChannel(channel(), UpdateReason.RSP2_RF_NOTCH_CONTROL,
            () -> channel().params().set(JAVA_BYTE, RxChannel.Offsets.RSP2_RF_NOTCH, RxChannel.flag(enabled)));
    }
}
