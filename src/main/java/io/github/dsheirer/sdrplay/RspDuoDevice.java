package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.Native;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

/**
 * An RSPduo, with two tuners. It is selected in one of the
 * {@link RspDuoMode}s; see {@link SDRplay#selectRspDuo}.
 *
 * <p>Tuner 1 ({@link Tuner#A}) has the Hi-Z and AM notch options and tuner 2
 * ({@link Tuner#B}) has the bias tee, so those methods need the matching tuner
 * to be selected.
 */
public final class RspDuoDevice extends RspDevice {
    private static final long EXT_REF = Layouts.offset(Layouts.DEV_PARAMS, "rspDuoParams", "extRefOutputEn");
    private static final long SAMPLE_FREQ = Layouts.offset(Layouts.DEVICE, "rspDuoSampleFreq");

    RspDuoDevice(Selection selection) {
        super(selection);
    }

    /** Returns the mode the device was selected in. */
    public RspDuoMode mode() {
        return rspDuoMode();
    }

    /**
     * Returns the settings of one tuner.
     *
     * @throws IllegalArgumentException if that tuner is not selected
     */
    public RxChannel channel(Tuner tuner) {
        RxChannel channel = channelOrNull(tuner);
        if (channel == null) {
            throw new IllegalArgumentException("Tuner " + tuner + " is not selected; selected: " + tuner());
        }
        return channel;
    }

    /**
     * Returns the sample rate in master, slave and dual tuner modes, 6 or 8 MHz,
     * which the API fixes for both tuners; 0 in single tuner mode.
     */
    public double rspDuoSampleRate() {
        checkOpen();
        return device().get(JAVA_DOUBLE, SAMPLE_FREQ);
    }

    /**
     * Changes the sample rate of dual tuner or master mode, between 6 and 8 MHz,
     * with {@code sdrplay_api_SwapRspDuoDualTunerModeSampleRate}.
     *
     * @return the sample rate before the change
     */
    public double swapDualTunerSampleRate(double hertz) throws SDRplayException {
        if (hertz != 6_000_000 && hertz != 8_000_000) {
            throw new IllegalArgumentException("Sample rate must be 6 or 8 MHz: " + hertz);
        }
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment current = arena.allocate(JAVA_DOUBLE);
            check("sdrplay_api_SwapRspDuoDualTunerModeSampleRate",
                Native.invoke(api.swapRspDuoDualTunerModeSampleRate, handle(), current, hertz));
            return current.get(JAVA_DOUBLE, 0);
        }
    }

    /** Returns true when the reference clock output is on. */
    public boolean isExternalReferenceOutput() {
        return devParams().get(JAVA_INT, EXT_REF) != 0;
    }

    /** Turns the reference clock output on or off. */
    public void setExternalReferenceOutput(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSPDUO_EXT_REF_CONTROL, () -> devParams().set(JAVA_INT, EXT_REF, enabled ? 1 : 0));
    }

    /** Returns true when the bias tee of tuner 2 is on. */
    public boolean isBiasT() {
        return channel(Tuner.B).params().get(JAVA_BYTE, RxChannel.Offsets.RSPDUO_BIAS_T) != 0;
    }

    /**
     * Turns the bias tee of tuner 2 on or off. This puts DC power on the
     * antenna connector, which can damage equipment not built for it.
     *
     * @throws IllegalArgumentException if tuner 2 is not selected
     */
    public void setBiasT(boolean enabled) throws SDRplayException {
        RxChannel channel = channel(Tuner.B);
        updateChannel(channel, UpdateReason.RSPDUO_BIAS_T_CONTROL,
            () -> channel.params().set(JAVA_BYTE, RxChannel.Offsets.RSPDUO_BIAS_T, RxChannel.flag(enabled)));
    }

    /** Returns the input tuner 1 uses below 60 MHz. */
    public RspDuoAmPort tuner1AmPort() {
        return RspDuoAmPort.fromCode(channel(Tuner.A).params().get(JAVA_INT, RxChannel.Offsets.RSPDUO_AM_PORT));
    }

    /**
     * Selects the input tuner 1 uses below 60 MHz.
     *
     * @throws IllegalArgumentException if tuner 1 is not selected
     */
    public void setTuner1AmPort(RspDuoAmPort port) throws SDRplayException {
        Objects.requireNonNull(port, "port");
        RxChannel channel = channel(Tuner.A);
        updateChannel(channel, UpdateReason.RSPDUO_AM_PORT_SELECT,
            () -> channel.params().set(JAVA_INT, RxChannel.Offsets.RSPDUO_AM_PORT, port.code()));
    }

    /** Returns true when the AM notch filter of tuner 1 is on. */
    public boolean isTuner1AmNotch() {
        return channel(Tuner.A).params().get(JAVA_BYTE, RxChannel.Offsets.RSPDUO_AM_NOTCH) != 0;
    }

    /**
     * Turns the AM broadcast notch filter of tuner 1 on or off.
     *
     * @throws IllegalArgumentException if tuner 1 is not selected
     */
    public void setTuner1AmNotch(boolean enabled) throws SDRplayException {
        RxChannel channel = channel(Tuner.A);
        updateChannel(channel, UpdateReason.RSPDUO_TUNER1_AM_NOTCH_CONTROL,
            () -> channel.params().set(JAVA_BYTE, RxChannel.Offsets.RSPDUO_AM_NOTCH, RxChannel.flag(enabled)));
    }

    /** Returns true when the broadcast FM notch filter of a tuner is on. */
    public boolean isRfNotch(Tuner tuner) {
        return channel(tuner).params().get(JAVA_BYTE, RxChannel.Offsets.RSPDUO_RF_NOTCH) != 0;
    }

    /** Turns the broadcast FM notch filter of a tuner on or off. */
    public void setRfNotch(Tuner tuner, boolean enabled) throws SDRplayException {
        RxChannel channel = channel(tuner);
        updateChannel(channel, UpdateReason.RSPDUO_RF_NOTCH_CONTROL,
            () -> channel.params().set(JAVA_BYTE, RxChannel.Offsets.RSPDUO_RF_NOTCH, RxChannel.flag(enabled)));
    }

    /** Returns true when the DAB notch filter of a tuner is on. */
    public boolean isRfDabNotch(Tuner tuner) {
        return channel(tuner).params().get(JAVA_BYTE, RxChannel.Offsets.RSPDUO_RF_DAB_NOTCH) != 0;
    }

    /** Turns the DAB notch filter of a tuner on or off. */
    public void setRfDabNotch(Tuner tuner, boolean enabled) throws SDRplayException {
        RxChannel channel = channel(tuner);
        updateChannel(channel, UpdateReason.RSPDUO_RF_DAB_NOTCH_CONTROL,
            () -> channel.params().set(JAVA_BYTE, RxChannel.Offsets.RSPDUO_RF_DAB_NOTCH, RxChannel.flag(enabled)));
    }
}
