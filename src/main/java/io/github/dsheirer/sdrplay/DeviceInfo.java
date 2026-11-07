package io.github.dsheirer.sdrplay;

import java.util.Set;

/**
 * A device reported by {@link SDRplay#devices()}.
 *
 * @param serialNumber the serial number
 * @param model the receiver model
 * @param hardwareVersion the raw hardware version the API reports, which identifies the model
 * @param tuners the tuners available to select: {@link Tuner#A} for every model except the
 *     RSPduo, which reports each tuner that no other application is using
 * @param rspDuoModes for an RSPduo, the modes it can be selected in now; empty for other models.
 *     Only {@link RspDuoMode#SLAVE} is offered while another application is using the device as master.
 * @param rspDuoSampleRate for an RSPduo offered as slave, the master's sample rate in Hz; otherwise 0
 * @param valid whether the API reports the entry as valid
 */
public record DeviceInfo(String serialNumber, RspModel model, int hardwareVersion, Set<Tuner> tuners,
        Set<RspDuoMode> rspDuoModes, double rspDuoSampleRate, boolean valid) {

    /** Makes the sets unmodifiable. */
    public DeviceInfo {
        tuners = Set.copyOf(tuners);
        rspDuoModes = Set.copyOf(rspDuoModes);
    }

    @Override
    public String toString() {
        return model.displayName() + " " + serialNumber;
    }
}
