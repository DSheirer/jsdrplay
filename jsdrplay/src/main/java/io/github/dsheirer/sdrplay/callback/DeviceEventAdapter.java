package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.parameter.event.EventParameters;
import io.github.dsheirer.sdrplay.parameter.event.EventType;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventCallback_t;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventParamsT;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for Device Event callbacks.  Implements foreign interface and transfers foreign memory event details to
 * native Java and invokes the appropriate interface methods for an event listener.
 */
public class DeviceEventAdapter implements sdrplay_api_EventCallback_t
{
    private static final Logger mLog = LoggerFactory.getLogger(DeviceEventAdapter.class);

    private ResourceScope mResourceScope;
    private IDeviceEventListener mDeviceEventListener;

    /**
     * Constructs an instance.
     * @param resourceScope to use in creating foreign memory segments to access foreign structures.
     * @param listener to receive translated device events.
     */
    public DeviceEventAdapter(ResourceScope resourceScope, IDeviceEventListener listener)
    {
        if(resourceScope == null)
        {
            throw new IllegalArgumentException("Resource scope must be non-null");
        }

        if(listener == null)
        {
            throw new IllegalArgumentException("Device event listener must be non-null");
        }

        mResourceScope = resourceScope;
        mDeviceEventListener = listener;
    }

    @Override
    public void apply(int eventTypeId, int tunerSelectId, MemoryAddress eventParametersPointer,
                      MemoryAddress callbackContext)
    {
        MemorySegment memorySegment = eventParametersPointer.asSegment(sdrplay_api_EventParamsT.sizeof(), mResourceScope);
        EventParameters eventParameters = new EventParameters(memorySegment);

        EventType eventType = EventType.fromValue(eventTypeId);
        TunerSelect tunerSelect = TunerSelect.fromValue(tunerSelectId);

        switch(eventType)
        {
            case GAIN_CHANGE -> {
                mDeviceEventListener.processGainChange(tunerSelect, eventParameters.getGainCallbackParameters(),
                        callbackContext);
            }
            case POWER_OVERLOAD_CHANGE -> {
                mDeviceEventListener.processPowerOverload(tunerSelect,
                        eventParameters.getPowerOverloadCallbackParameters(), callbackContext);
            }
            case DEVICE_REMOVED -> {
                mDeviceEventListener.processDeviceRemoval(tunerSelect, callbackContext);
            }
            case RSP_DUO_MODE_CHANGE -> {
                mDeviceEventListener.processRspDuoModeChange(tunerSelect,
                        eventParameters.getRspDuoModeCallbackParameters(), callbackContext);
            }
            case UNKNOWN -> {
                mLog.warn("Unknown device event callback ignored.  Please contact the library developer as this may " +
                        "indicate a change to the SDRPlay API change. Tuner:" + tunerSelect + " Event Type ID:" +
                        eventTypeId);
            }
            default -> {
                throw new IllegalStateException("DeviceEventAdapter must be updated handle EventType." + eventType);
            }
        }
    }
}
