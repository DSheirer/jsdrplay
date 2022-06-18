package com.github.dsheirer.sdrplay.callback;

import com.github.dsheirer.sdrplay.device.TunerSelect;

public interface IStreamCallbackListener
{
    /**
     * Process stream callback parameters and reset value received over the stream callback interface.
     * @param parameters to process
     * @param reset value
     */
    void process(TunerSelect tunerSelect, StreamCallbackParameters parameters, int reset);
}
