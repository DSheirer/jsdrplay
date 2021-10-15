package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.device.TunerSelect;
import io.github.dsheirer.sdrplay.parameter.event.EventType;
import io.github.dsheirer.sdrplay.parameter.event.GainCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.PowerOverloadCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.RspDuoModeCallbackParameters;

/**
 * Device event listener.  Receives events for the device, impacting one or both (if equipped) tuners on the device.
 */
public interface IDeviceEventListener
{
    /**
     * Process a device event
     * @param eventType identifies the type of event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     */
    void processEvent(EventType eventType, TunerSelect tunerSelect);

    /**
     * Process a gain change event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param gainCallbackParameters containing event details
     */
    void processGainChange(TunerSelect tunerSelect, GainCallbackParameters gainCallbackParameters);

    /**
     * Process a power overload event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param parameters containing event details
     */
    void processPowerOverload(TunerSelect tunerSelect, PowerOverloadCallbackParameters parameters);

    /**
     * Process an RSP-Duo mode change event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param parameters containing event details
     */
    void processRspDuoModeChange(TunerSelect tunerSelect, RspDuoModeCallbackParameters parameters);

    /**
     * Process a device removed (ie unplugged) event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     */
    void processDeviceRemoval(TunerSelect tunerSelect);

}
