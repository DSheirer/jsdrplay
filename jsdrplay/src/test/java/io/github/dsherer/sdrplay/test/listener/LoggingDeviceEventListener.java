package io.github.dsherer.sdrplay.test.listener;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.callback.IDeviceEventListener;
import io.github.dsheirer.sdrplay.device.Device;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import io.github.dsheirer.sdrplay.parameter.event.EventType;
import io.github.dsheirer.sdrplay.parameter.event.GainCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.PowerOverloadCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.RspDuoModeCallbackParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging device event listener for debug or testing.
 */
public class LoggingDeviceEventListener implements IDeviceEventListener
{
    private static final Logger mLog = LoggerFactory.getLogger(LoggingDeviceEventListener.class);
    private String mLabel;
    private Device mDevice;

    /**
     * Constructs an instance
     * @param label prefix for logging.
     */
    public LoggingDeviceEventListener(String label, Device device)
    {
        mLabel = label;
        mDevice = device;
    }

    @Override
    public void processEvent(EventType eventType, TunerSelect tunerSelect)
    {
        mLog.info(mLabel + " - Unrecognized Event: " + eventType + " Tuner:" + tunerSelect);
    }

    @Override
    public void processGainChange(TunerSelect tunerSelect, GainCallbackParameters gainCallbackParameters)
    {
        mLog.info(mLabel + " - Gain Change - Tuner: " + tunerSelect + " " + gainCallbackParameters.toString());
    }

    @Override
    public void processPowerOverload(TunerSelect tunerSelect, PowerOverloadCallbackParameters parameters)
    {
        mLog.info(mLabel + " - Power Overload - Tuner: " + tunerSelect + " - acknowledging");

        try
        {
            mDevice.acknowledgePowerOverload(tunerSelect);
        }
        catch(SDRplayException se)
        {
            mLog.error("Unable to acknowledge power overload for tuner(s): " + tunerSelect + ": " + se.getLocalizedMessage());
        }
    }

    @Override
    public void processRspDuoModeChange(TunerSelect tunerSelect, RspDuoModeCallbackParameters parameters)
    {
        mLog.info(mLabel + " - RSPduo Mode Change - Tuner: " + tunerSelect + " Event:" + parameters.getRspDuoModeEvent());
    }

    @Override
    public void processDeviceRemoval(TunerSelect tunerSelect)
    {
        mLog.info(mLabel + " - Device Removed");
    }
}
