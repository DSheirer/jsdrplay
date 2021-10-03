package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.Rsp1DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp1TunerParameters;

/**
 * RSP1 Tuner
 */
public class Rsp1Tuner extends RspTuner<Rsp1DeviceParameters, Rsp1TunerParameters>
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
    public Rsp1Tuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, Rsp1DeviceParameters deviceParameters,
                     Rsp1TunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, tunerSelect, deviceParameters, tunerParameters, controlParameters);
    }
}
