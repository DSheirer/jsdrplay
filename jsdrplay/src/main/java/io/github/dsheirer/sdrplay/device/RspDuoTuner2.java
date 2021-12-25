package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;

/**
 * RSPduo Tuner 2
 */
public class RspDuoTuner2 extends RspDuoTuner
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
    public RspDuoTuner2(Device device, SDRplay sdrplay, RspDuoDeviceParameters deviceParameters,
                        RspDuoTunerParameters tunerParameters, ControlParameters controlParameters)
    {
        super(device, sdrplay, TunerSelect.TUNER_2, deviceParameters, tunerParameters, controlParameters);
    }

    /**
     * Indicates if the Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return getTunerParameters().isBiasT();
    }

    /**
     * Enables or disables the Bias-T
     * @param enable value
     * @throws SDRplayException if there is an error
     */
    public void setBiasT(boolean enable) throws SDRplayException
    {
        getTunerParameters().setBiasT(enable);
        update(UpdateReason.RSP_DUO_BIAS_T_CONTROL);
    }
}
