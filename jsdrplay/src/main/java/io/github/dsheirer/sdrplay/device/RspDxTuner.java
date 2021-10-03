package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDxDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDxTunerParameters;

/**
 * RSPdx Tuner
 */
public class RspDxTuner extends RspTuner<RspDxDeviceParameters, RspDxTunerParameters>
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
    public RspDxTuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, RspDxDeviceParameters deviceParameters,
                      RspDxTunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, tunerSelect, deviceParameters, tunerParameters, controlParameters);
    }
}
