package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDxDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.HdrModeBandwidth;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDxAntenna;
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

    /**
     * Indicates if HDR mode is enabled
     */
    public boolean isHdrMode()
    {
        return getDeviceParameters().isHdr();
    }

    /**
     * Enables or disables HDR mode
     * @param enable mode
     * @throws SDRplayException if there is an error
     */
    public void setHdrMode(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setHdr(enable);
        update(UpdateReason.EXTENSION_RSP_DX_HDR_ENABLE);
    }

    /**
     * HDR mode bandwidth
     */
    public HdrModeBandwidth getHdrModeBandwidth()
    {
        return getTunerParameters().getHdrModeBandwidth();
    }

    /**
     * Sets HDR mode bandwidth
     * @param bandwidth to select
     * @throws SDRplayException if there is an error
     */
    public void setHdrModeBandwidth(HdrModeBandwidth bandwidth) throws SDRplayException
    {
        getTunerParameters().setHdrModeBandwidth(bandwidth);
        update(UpdateReason.EXTENSION_RSP_DX_HDR_BANDWIDTH);
    }

    /**
     * Indicates if the RF notch is enabled
     */
    public boolean isRFNotch()
    {
        return getDeviceParameters().isRfNotch();
    }

    /**
     * Enables or disables the RF notch
     * @param enable setting
     * @throws SDRplayException if there is an error
     */
    public void setRFNotch(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setRfNotch(enable);
        update(UpdateReason.EXTENSION_RSP_DX_RF_NOTCH_CONTROL);
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
        update(UpdateReason.EXTENSION_RSP_DX_RF_DAB_NOTCH_CONTROL);
    }

    /**
     * Indicates if the Bias-T is enabled
     */
    public boolean isBiasT()
    {
        return getDeviceParameters().isBiasT();
    }

    /**
     * Enables or disables the Bias-T
     * @param enable value
     * @throws SDRplayException if there is an error
     */
    public void setBiasT(boolean enable) throws SDRplayException
    {
        getDeviceParameters().setBiasT(enable);
        update(UpdateReason.EXTENSION_RSP_DX_BIAS_T_CONTROL);
    }

    /**
     * Antenna selection
     */
    public RspDxAntenna getAntenna()
    {
        return getDeviceParameters().getRspDxAntenna();
    }

    /**
     * Sets the antenna selection
     * @param antenna to select
     * @throws SDRplayException if there is an error
     */
    public void setAntenna(RspDxAntenna antenna) throws SDRplayException
    {
        getDeviceParameters().setRspDxAntenna(antenna);
        update(UpdateReason.EXTENSION_RSP_DX_ANTENNA_CONTROL);
    }
}
