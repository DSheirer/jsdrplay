package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoAmPort;
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

    /**
     * Indicates which AM port is selected
     */
    public RspDuoAmPort getAmPort()
    {
        return getTunerParameters().getTuner1AmPort();
    }

    /**
     * Selects the AM port to use
     * @param port to use
     * @throws SDRplayException if there is an error
     */
    public void setAmPort(RspDuoAmPort port) throws SDRplayException
    {
        getTunerParameters().setTuner1AmPort(port);
        update(UpdateReason.RSP_DUO_AM_PORT_SELECT);
    }

    /**
     * Indicates if the AM notch is enabled
     */
    public boolean isAmNotch()
    {
        return getTunerParameters().isTuner1AmNotch();
    }

    /**
     * Enables or disables the AM notch
     * @param enable for the notch
     * @throws SDRplayException if there is an error
     */
    public void setAmNotch(boolean enable) throws SDRplayException
    {
        getTunerParameters().setTuner1AmNotch(enable);
        update(UpdateReason.RSP_DUO_TUNER_1_AM_NOTCH_CONTROL);
    }
}
