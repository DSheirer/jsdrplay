package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.Rsp2DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp2TunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;

/**
 * RSPduo Tuner
 */
public abstract class RspDuoTuner extends RspTuner<RspDuoDeviceParameters, RspDuoTunerParameters>
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
    public RspDuoTuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, RspDuoDeviceParameters deviceParameters,
                       RspDuoTunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, tunerSelect, deviceParameters, tunerParameters, controlParameters);
    }
}
