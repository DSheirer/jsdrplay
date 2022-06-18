package com.github.dsheirer.sdrplay.callback;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCbParamsT;
import com.github.dsheirer.sdrplay.device.TunerSelect;
import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCallback_t;
import com.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;

/**
 * I/Q sample stream callback adapter.  Implements the native interface and transfers the native callback data to an
 * implementation of the Java IStreamListener interface.
 */
public class StreamCallbackAdapter implements sdrplay_api_StreamCallback_t
{
    private ResourceScope mResourceScope;
    private IStreamListener mStreamListener;
    private TunerSelect mTunerSelect;
    private IStreamCallbackListener mStreamCallbackListener;

    /**
     * Constructs an instance of the callback implementation
     * @param resourceScope for defining new foreign memory segments
     * @param streamListener to receive transferred I/Q samples and event details
     * @param tunerSelect identifies the tuner for this adapter
     * @param listener to receive callback parameters
     */
    public StreamCallbackAdapter(ResourceScope resourceScope, IStreamListener streamListener,
                                 TunerSelect tunerSelect, IStreamCallbackListener listener)
    {
        if(resourceScope == null)
        {
            throw new IllegalArgumentException("Resource scope must be non-null");
        }

        mResourceScope = resourceScope;
        mTunerSelect = tunerSelect;
        mStreamCallbackListener = listener;
        setListener(streamListener);
    }

    /**
     * Updates the listener for receiving samples
     * @param listener to receive stream samples
     */
    public void setListener(IStreamListener listener)
    {
        mStreamListener = listener;
    }

    /**
     * Receives callback of foreign memory data, transfers it to Java, and passes to the listener.
     * @param iSamplesPointer array foreign memory address
     * @param qSamplesPointer array foreign memory address
     * @param parametersPointer associated with the callback - foreign memory address
     * @param sampleCount number of samples in each of the I and Q arrays
     * @param reset 0 or 1, translated to a boolean
     * @param deviceContext of the device that sourced the samples
     */
    @Override
    public void apply(MemoryAddress iSamplesPointer, MemoryAddress qSamplesPointer, MemoryAddress parametersPointer,
                      int sampleCount, int reset, MemoryAddress deviceContext)
    {
        if(mStreamListener != null || mStreamCallbackListener != null)
        {
            //Translate the callback parameters pointer to a memory segment and re-construct the parameters as a Java object
            StreamCallbackParameters parameters = new StreamCallbackParameters(sdrplay_api_StreamCbParamsT
                    .ofAddress(parametersPointer, mResourceScope));

            if(mStreamCallbackListener != null)
            {
                mStreamCallbackListener.process(mTunerSelect, parameters, reset);
            }

            if(mStreamListener != null)
            {
                //Copy the I/Q samples from foreign memory to native java arrays
                short[] i = new short[sampleCount];
                short[] q = new short[sampleCount];

                for(int x = 0; x < sampleCount; x++)
                {
                    i[x] = iSamplesPointer.get(ValueLayout.JAVA_SHORT, x);
                    q[x] = qSamplesPointer.get(ValueLayout.JAVA_SHORT, x);
                }

                mStreamListener.processStream(i, q, parameters, Flag.evaluate(reset));
            }
        }
    }
}
