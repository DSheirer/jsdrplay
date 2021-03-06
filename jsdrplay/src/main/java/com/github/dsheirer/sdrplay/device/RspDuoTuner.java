package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.UpdateReason;
import com.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import com.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;

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

    /**
     * Indicates if the RF notch is enabled
     */
    public boolean isRFNotch()
    {
        return getTunerParameters().isRfNotch();
    }

    /**
     * Enables or disables the RF notch
     * @param enable setting
     * @throws SDRplayException if there is an error
     */
    public void setRFNotch(boolean enable) throws SDRplayException
    {
        getTunerParameters().setRfNotch(enable);
        update(UpdateReason.RSP_DUO_RF_NOTCH_CONTROL);
    }

    /**
     * Indicates if the RF DAB notch is enabled
     */
    public boolean isRfDabNotch()
    {
        return getTunerParameters().isRfDabNotch();
    }

    /**
     * Enables or disables the RF DAB notch
     * @param enable value
     * @throws SDRplayException if there is an error
     */
    public void setRfDabNotch(boolean enable) throws SDRplayException
    {
        getTunerParameters().setRfDabNotch(enable);
        update(UpdateReason.RSP_DUO_RF_DAB_NOTCH_CONTROL);
    }

    /**
     * Indicates if the external reference output is enabled.
     */
    public boolean isExternalReferenceOutput()
    {
        return getDeviceParameters().isExternalReferenceOutput();
    }

    /**
     * Enables or disables the external reference output
     * @param enable value
     * @throws SDRplayException if there is an error
     */
    public void setExternalReferenceOutput(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setExternalReferenceOutput(enable);
        update(UpdateReason.RSP_DUO_EXT_REF_CONTROL);
    }
}
