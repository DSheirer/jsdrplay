package com.github.dsheirer.sdrplay.callback;

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
     */
    void processStream(short[] xi, short[] xq, StreamCallbackParameters streamCallbackParameters,
                       boolean reset);
}
