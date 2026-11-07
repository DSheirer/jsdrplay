package io.github.dsheirer.sdrplay;

/** An event delivered to a {@link DeviceEventListener}. */
public sealed interface DeviceEvent {
    /** Returns the tuner the event is about. */
    Tuner tuner();

    /**
     * The gain of a tuner changed, for example through the AGC.
     *
     * @param tuner the tuner
     * @param gainReductionDb the baseband gain reduction, in dB
     * @param lnaGainReductionDb the gain reduction of the LNA state, in dB
     * @param currentGainDb the overall gain, in dB
     */
    record GainChanged(Tuner tuner, int gainReductionDb, int lnaGainReductionDb, double currentGainDb)
            implements DeviceEvent {
    }

    /**
     * The input power of a tuner went above or back below the overload threshold.
     *
     * @param tuner the tuner
     * @param overloaded true when an overload was detected, false when it was corrected
     */
    record PowerOverload(Tuner tuner, boolean overloaded) implements DeviceEvent {
    }

    /**
     * The other side of an RSPduo master and slave pair changed state.
     *
     * @param tuner the tuner
     * @param change what happened
     */
    record RspDuoModeChanged(Tuner tuner, RspDuoModeChange change) implements DeviceEvent {
    }

    /**
     * The device was unplugged. Close it, from a thread other than the
     * listener's.
     *
     * @param tuner the tuner
     */
    record DeviceRemoved(Tuner tuner) implements DeviceEvent {
    }

    /**
     * The device stopped working. Close it, from a thread other than the
     * listener's.
     *
     * @param tuner the tuner
     */
    record DeviceFailure(Tuner tuner) implements DeviceEvent {
    }
}
