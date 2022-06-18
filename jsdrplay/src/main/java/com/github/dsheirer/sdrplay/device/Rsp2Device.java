package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.parameter.composite.Rsp2CompositeParameters;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.SDRplay;

/**
 * RSP2 Device
 */
public class Rsp2Device extends Device<Rsp2CompositeParameters, Rsp2Tuner>
{
    private Rsp2Tuner mTuner1;

    /**
     * Constructs an SDRPlay RSP2 device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param deviceStruct parser
     */
    Rsp2Device(SDRplay sdrPlay, IDeviceStruct deviceStruct)
    {
        super(sdrPlay, deviceStruct);
    }

    @Override
    public Rsp2Tuner getTuner() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new Rsp2Tuner(Rsp2Device.this, getAPI(), getTunerSelect(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerAParameters(),
                    getCompositeParameters().getControlAParameters());
        }

        return mTuner1;
    }
}
