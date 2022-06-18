package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.UpdateReason;
import com.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import com.github.dsheirer.sdrplay.parameter.device.Rsp2DeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp2AmPort;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp2Antenna;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp2TunerParameters;

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
        update(UpdateReason.RSP2_EXT_REF_CONTROL);
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
        update(UpdateReason.RSP2_BIAS_T_CONTROL);
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
        update(UpdateReason.RSP2_RF_NOTCH_CONTROL);
    }

    /**
     * AM port selection
     */
    public Rsp2AmPort getAmPort()
    {
        return getTunerParameters().getAmPort();
    }

    /**
     * Sets the AM port
     * @param port to select
     * @throws SDRplayException if there is an error
     */
    public void setAmPort(Rsp2AmPort port) throws SDRplayException
    {
        getTunerParameters().setAmPort(port);
        update(UpdateReason.RSP2_AM_PORT_SELECT);
    }

    /**
     * Antenna selection
     */
    public Rsp2Antenna getAntenna()
    {
        return getTunerParameters().getAntenna();
    }

    /**
     * Sets the antenna
     * @param antenna to select
     * @throws SDRplayException if there is an error
     */
    public void setAntenna(Rsp2Antenna antenna) throws SDRplayException
    {
        getTunerParameters().setAntenna(antenna);
        update(UpdateReason.RSP2_ANTENNA_CONTROL);
    }
}
