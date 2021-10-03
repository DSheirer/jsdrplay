package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.Rsp1DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.device.Rsp2DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp1TunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp2TunerParameters;

/**
 * RSP2 Tuner
 */
public class Rsp2Tuner extends RspTuner<Rsp2DeviceParameters, Rsp2TunerParameters>
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
    public Rsp2Tuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, Rsp2DeviceParameters deviceParameters,
                     Rsp2TunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, tunerSelect, deviceParameters, tunerParameters, controlParameters);
    }
}
