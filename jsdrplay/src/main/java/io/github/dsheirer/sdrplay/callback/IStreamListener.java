package io.github.dsheirer.sdrplay.callback;

import jdk.incubator.foreign.MemoryAddress;

/**
 * Stream Listener interface.
 */
public interface IStreamListener
{
    /**
     * Process samples from a single stream of I/Q samples
     * @param xi array of Inphase samples
     * @param xq array of Quadrature samples
     * @param streamCallbackParameters stream callback parameters
     * @param reset indicates if a re-initialization has occurred within the API and that local buffering should be reset
     * @param context pointer used to initialize the device
     */
    void processStreamA(short[] xi, short[] xq, StreamCallbackParameters streamCallbackParameters,
                        boolean reset, MemoryAddress context);
}
