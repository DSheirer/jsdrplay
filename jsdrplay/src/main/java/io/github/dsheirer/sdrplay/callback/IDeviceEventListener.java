package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.parameter.event.EventParameters;
import io.github.dsheirer.sdrplay.parameter.event.EventType;
import io.github.dsheirer.sdrplay.parameter.event.GainCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.PowerOverloadCallbackParameters;
import io.github.dsheirer.sdrplay.parameter.event.RspDuoModeCallbackParameters;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import jdk.incubator.foreign.MemoryAddress;

/**
 * Device event listener.  Receives events for the device, impacting one or both (if equipped) tuners on the device.
 */
public interface IDeviceEventListener
{
    /**
     * Process a device event
     * @param eventType identifies the type of event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param eventParameters
     * @param context that was used to initialize the device
     */
    void processEvent(EventType eventType, TunerSelect tunerSelect, EventParameters eventParameters, MemoryAddress context);

    //TODO: refactor the event into the 3 types of events that can occur and create methods for each, so that the
    //TODO: user doesn't have to detect the event type and then access the correct event parameters from the 3 possible.

    /**
     * Process a gain change event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param gainCallbackParameters containing event details
     * @param deviceContext pointer used to initialize the device
     */
    void processGainChange(TunerSelect tunerSelect, GainCallbackParameters gainCallbackParameters, MemoryAddress deviceContext);

    /**
     * Process a power overload event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param parameters containing event details
     * @param deviceContext pointer used to initialize the device
     */
    void processPowerOverload(TunerSelect tunerSelect, PowerOverloadCallbackParameters parameters, MemoryAddress deviceContext);

    /**
     * Process an RSP-Duo mode change event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param parameters containing event details
     * @param deviceContext pointer used to initialize the device
     */
    void processRspDuoModeChange(TunerSelect tunerSelect, RspDuoModeCallbackParameters parameters, MemoryAddress deviceContext);

    /**
     * Process a device removed (ie unplugged) event
     * @param tunerSelect identifies which tuner(s) are included in the event (A, B, BOTH, or NEITHER)
     * @param deviceContext pointer used to initialize the device
     */
    void processDeviceRemoval(TunerSelect tunerSelect, MemoryAddress deviceContext);

}
