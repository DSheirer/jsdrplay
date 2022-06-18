package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.UpdateReason;
import com.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import com.github.dsheirer.sdrplay.parameter.device.Rsp1aDeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp1aTunerParameters;

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

    /**
     * Indicates if the RF notch is enabled
     */
    public boolean isRfNotch()
    {
        return getDeviceParameters().isRFNotch();
    }

    /**
     * Enables or disables the RF notch
     * @param enable setting
     * @throws SDRplayException if there is an error
     */
    public void setRfNotch(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setRFNotch(enable);
        update(UpdateReason.RSP1A_RF_NOTCH_CONTROL);
    }

    /**
     * Indicates if the RF DAB notch is enabled
     */
    public boolean isRfDabNotch()
    {
        return getDeviceParameters().isRfDabNotch();
    }

    /**
     * Enables or disables the RF DAB notch
     * @param enable value
     * @throws SDRplayException if there is an error
     */
    public void setRfDabNotch(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setRfDabNotch(enable);
        update(UpdateReason.RSP1A_RF_DAB_NOTCH_CONTROL);
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
        update(UpdateReason.RSP1A_BIAS_T_CONTROL);
    }
}
