package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.callback.IDeviceEventListener;
import com.github.dsheirer.sdrplay.callback.IStreamListener;
import com.github.dsheirer.sdrplay.parameter.composite.RspDuoCompositeParameters;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import com.github.dsheirer.sdrplay.util.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Composite device encapsulating two RSPduo device instances, one configured as master for tuner 1 and the other
 * configured as slave for tuner 2.
 *
 * In this configuration, both tuners are initialized and de-initialized at the same time to ensure that the master
 * device is initialized first so that tuner 2 can be accessed.  Likewise, it ensures that the slave device is
 * de-initialized first, so that tuner 1 can be de-initialized.
 */
public class RspDuoDualIndependentTunerDevice extends Device<RspDuoCompositeParameters, RspDuoTuner1>
{
    private Logger mLog = LoggerFactory.getLogger(RspDuoDualIndependentTunerDevice.class);

    private RspDuoDevice mMasterDevice;
    private RspDuoDevice mSlaveDevice;

    /**
     * Constructs a composite SDRPlay RSPduo device with support for dual independent tuners.
     */
    public RspDuoDualIndependentTunerDevice(RspDuoDevice masterDevice, RspDuoDevice slaveDevice)
    {
        super(masterDevice.getAPI(), masterDevice.getDeviceStruct());

        mMasterDevice = masterDevice;
        mSlaveDevice = slaveDevice;

        if(!mMasterDevice.isSelected() || !mSlaveDevice.isSelected())
        {
            throw new IllegalStateException("Master and Slave devices must both be selected prior to constructing " +
                    "composite device");
        }

        mSelected = true;
    }

    /**
     * Tuner 1 configured as master
     * @return tuner
     * @throws SDRplayException if there is an issue accessing the tuner
     */
    @Override
    public RspDuoTuner1 getTuner() throws SDRplayException
    {
        return (RspDuoTuner1) mMasterDevice.getTuner();
    }

    /**
     * Tuner 2 configured as the slave device
     * @return tuner 2
     * @throws SDRplayException if there is an issue accessing the tuner
     */
    public RspDuoTuner2 getTuner2() throws SDRplayException
    {
        return (RspDuoTuner2) mSlaveDevice.getTuner();
    }

    /**
     * Overrides the parent select() method to be a non-operation.  Both master and slave devices should already be
     * selected before they are joined via this composite device.
     * @throws SDRplayException always.
     */
    @Override
    public void select() throws SDRplayException
    {
        throw new SDRplayException("Device has already been selected");
    }

    /**
     * Un-initializes both the master and slave devices
     * @throws SDRplayException if there is an error
     */
    @Override
    public void uninitialize() throws SDRplayException
    {
        mLog.info("Master Sample Rate: " + mMasterDevice.getRspDuoSampleFrequency());
        mLog.info("Slave Sample Rate: " + mSlaveDevice.getRspDuoSampleFrequency());

        if(isInitialized())
        {
            //Uninit slave first and then master
            Retry.quietly(() -> mSlaveDevice.uninitialize());
            Retry.quietly(() -> mMasterDevice.uninitialize());
            mInitialized = false;
        }
        else
        {
            throw new SDRplayException("Attempt to un-init a device that has not been initialized previously");
        }
    }

    /**
     * Initializes this device for use and starts the tuners providing raw signal samples to the stream listener(s) and
     * device events to the event listener.
     *
     * @param eventListener to receive device event notifications
     * @param streamListeners two stream listeners to receive samples from the tuner where the first listener
     * will receive samples from tuner 1 and the second listener will receive samples from tuner 2.
     *
     * @throws SDRplayException if there is an error
     */
    @Override
    public void init(IDeviceEventListener eventListener, IStreamListener... streamListeners) throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before it can be initialized");
        }

        if(isInitialized())
        {
            throw new SDRplayException("Device has already been initialized with listeners");
        }

        List<IStreamListener> listeners = Arrays.stream(streamListeners).toList();

        if(listeners.size() != 2)
        {
            throw new SDRplayException("Requires 2 stream listeners");
        }


        Retry.quietly(() -> mMasterDevice.init(eventListener, listeners.get(0), null));
        Retry.quietly(() -> mSlaveDevice.init(eventListener, listeners.get(1), null));
        mInitialized = true;
    }

    @Override
    public void release() throws SDRplayException
    {
        if(selected())
        {
            mSelected = false;
            mMasterDevice.release();

            //Releasing the master device is not instantaneous ... have to attempt slave release multiple times
            Retry.quietly(5, 20, () -> mSlaveDevice.release());
        }
    }

    /**
     * Sets the tuner bandwidth and final decimation for both the master and the slave devices.
     *
     * Note: this does not set the initial sample rate which can only be 6 or 8 MHz and which must also be set on the
     * master device before the device is selected.
     *
     * @param sampleRate to apply only the bandwidth and final decimation values
     * @throws SDRplayException if there is an error
     */
    @Override
    public void setSampleRate(SampleRate sampleRate) throws SDRplayException
    {
        mMasterDevice.getTuner().setBandwidth(sampleRate.getBandwidth());
        mMasterDevice.setDecimation(sampleRate.getDecimation());

        mSlaveDevice.getTuner().setBandwidth(sampleRate.getBandwidth());
        mSlaveDevice.setDecimation(sampleRate.getDecimation());
    }

    /**
     * Acknowledge tuner power overload events
     * @param tunerSelect identifying which tuner(s)
     * @throws SDRplayException on error
     */
    public void acknowledgePowerOverload(TunerSelect tunerSelect) throws SDRplayException
    {
        switch(tunerSelect)
        {
            case TUNER_1 -> mMasterDevice.acknowledgePowerOverload(tunerSelect);
            case TUNER_2 -> mSlaveDevice.acknowledgePowerOverload(tunerSelect);
            case TUNER_BOTH -> {
                mMasterDevice.acknowledgePowerOverload(TunerSelect.TUNER_1);
                mSlaveDevice.acknowledgePowerOverload(TunerSelect.TUNER_2);
            }
            case NEITHER -> {}
        }
    }
}
