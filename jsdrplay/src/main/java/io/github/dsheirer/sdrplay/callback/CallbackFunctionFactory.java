package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_CallbackFnsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventCallback_t;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCallback_t;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Callback functions (sdrplay_api_CallbackFnsT) factory
 */
public class CallbackFunctionFactory
{
    /**
     * Creates a foreign memory union of callback functions used when initializing and SDRPlay device via the API.
     * @param scope used to allocate foreign memory
     * @param eventListener to receive device events
     * @param streamAListener to receive streaming I/Q data from Tuner A
     * @param streamBListener to receive streaming I/Q data from Tuner B
     * @return foreign memory segment for the functions.
     */
    public static MemorySegment create(ResourceScope scope, IDeviceEventListener eventListener,
                                       IStreamListener streamAListener, IStreamListener streamBListener)
    {
        //Create the event callback function
        sdrplay_api_EventCallback_t eventCallback = new DeviceEventAdapter(scope, eventListener);
        MemoryAddress eventFunction = sdrplay_api_EventCallback_t.allocate(eventCallback, scope);

        //Create the stream A callback function
        sdrplay_api_StreamCallback_t streamACallback = new StreamCallbackAdapter(scope, streamAListener);
        MemoryAddress streamAFunction = sdrplay_api_StreamCallback_t.allocate(streamACallback, scope);

        //Create the stream B callback function
        sdrplay_api_StreamCallback_t streamBCallback = new StreamCallbackAdapter(scope, streamBListener);
        MemoryAddress streamBFunction = sdrplay_api_StreamCallback_t.allocate(streamBCallback, scope);

        //Create the callback functions union and populate the callback functions
        MemorySegment callbackFunctions = sdrplay_api_CallbackFnsT.allocate(scope);
        sdrplay_api_CallbackFnsT.EventCbFn$set(callbackFunctions, eventFunction);
        sdrplay_api_CallbackFnsT.StreamACbFn$set(callbackFunctions, streamAFunction);
        sdrplay_api_CallbackFnsT.StreamBCbFn$set(callbackFunctions, streamBFunction);

        return callbackFunctions;
    }
}
