package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.Rsp1aDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp1aTunerParameters;

/**
 * RSP1A Tuner
 */
public class Rsp1aTuner extends RspTuner<Rsp1aDeviceParameters, Rsp1aTunerParameters>
{
    /**
     * Constructs an instance
     * @param device parent for this tuner
     * @param sdrplay api
     * @param tunerSelect to specify which tuner
     * @param deviceParameters for this device
     * @param tunerParameters for this tuner
     * @param controlParameters for this device
     */
    public Rsp1aTuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, Rsp1aDeviceParameters deviceParameters,
                      Rsp1aTunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, tunerSelect, deviceParameters, tunerParameters, controlParameters);
    }
}
