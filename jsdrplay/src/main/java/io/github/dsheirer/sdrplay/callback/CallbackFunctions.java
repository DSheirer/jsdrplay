package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_CallbackFnsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventCallback_t;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCallback_t;
import io.github.dsheirer.sdrplay.device.TunerSelect;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.NativeSymbol;
import jdk.incubator.foreign.ResourceScope;

/**
 * Callback functions (sdrplay_api_CallbackFnsT) factory
 */
public class CallbackFunctions
{
    private DeviceEventAdapter mDeviceEventAdapter;
    private StreamCallbackAdapter mStreamACallbackAdapter;
    private StreamCallbackAdapter mStreamBCallbackAdapter;
    private MemorySegment mCallbackFunctionsMemorySegment;

    public CallbackFunctions(ResourceScope resourceScope, IDeviceEventListener deviceEventListener,
                             IStreamListener streamAListener, IStreamListener streamBListener,
                             IStreamCallbackListener streamCallbackListener)
    {
        //Create the event callback function
        mDeviceEventAdapter = new DeviceEventAdapter(resourceScope, deviceEventListener);
        NativeSymbol eventFunction = sdrplay_api_EventCallback_t.allocate(mDeviceEventAdapter, resourceScope);

        //Create the stream A callback function
        mStreamACallbackAdapter = new StreamCallbackAdapter(resourceScope, streamAListener, TunerSelect.TUNER_1,
                streamCallbackListener);
        NativeSymbol streamAFunction = sdrplay_api_StreamCallback_t.allocate(mStreamACallbackAdapter, resourceScope);

        //Create the stream B callback function
        mStreamBCallbackAdapter = new StreamCallbackAdapter(resourceScope, streamBListener, TunerSelect.TUNER_2,
                streamCallbackListener);
        NativeSymbol streamBFunction = sdrplay_api_StreamCallback_t.allocate(mStreamBCallbackAdapter, resourceScope);

        //Create the callback functions union and populate the callback functions
        mCallbackFunctionsMemorySegment = sdrplay_api_CallbackFnsT.allocate(resourceScope);
        sdrplay_api_CallbackFnsT.EventCbFn$set(mCallbackFunctionsMemorySegment, eventFunction.address());
        sdrplay_api_CallbackFnsT.StreamACbFn$set(mCallbackFunctionsMemorySegment, streamAFunction.address());
        sdrplay_api_CallbackFnsT.StreamBCbFn$set(mCallbackFunctionsMemorySegment, streamBFunction.address());
    }

    /**
     * Foreign memory segment for the callback functions.
     */
    public MemorySegment getCallbackFunctionsMemorySegment()
    {
        return mCallbackFunctionsMemorySegment;
    }

    /**
     * Updates the device event listener
     * @param listener to receive device events
     */
    public void setDeviceEventListener(IDeviceEventListener listener)
    {
        mDeviceEventAdapter.setListener(listener);
    }

    /**
     * Updates the stream A listener
     * @param listener to receive samples for stream A
     */
    public void setStreamAListener(IStreamListener listener)
    {
        mStreamACallbackAdapter.setListener(listener);
    }

    /**
     * Updates the stream B listener
     * @param listener to receive samples for stream B
     */
    public void setStreamBListener(IStreamListener listener)
    {
        mStreamBCallbackAdapter.setListener(listener);
    }
}
