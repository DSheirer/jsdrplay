package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;

/**
 * RSPduo Tuner 1
 */
public class RspDuoTuner1 extends RspDuoTuner
{
    /**
     * Constructs an instance
     *
     * @param device parent for this tuner
     * @param sdrplay api
     * @param deviceParameters for this device
     * @param tunerParameters for this tuner
     * @param controlParameters for this device
     */
    public RspDuoTuner1(Device device, SDRplay sdrplay, RspDuoDeviceParameters deviceParameters,
                        RspDuoTunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, TunerSelect.TUNER_1, deviceParameters, tunerParameters, controlParameters);
    }
}
