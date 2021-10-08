package io.github.dsheirer.sdrplay.callback;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCallback_t;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_StreamCbParamsT;
import io.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * I/Q sample stream callback adapter.  Implements the native interface and transfers the native callback data to an
 * implementation of the Java IStreamListener interface.
 */
public class StreamCallbackAdapter implements sdrplay_api_StreamCallback_t
{
    private ResourceScope mResourceScope;
    private IStreamListener mStreamListener;

    /**
     * Constructs an instance of the callback implementation
     * @param resourceScope for defining new foreign memory segments
     * @param streamListener to receive transferred I/Q samples and event details
     */
    public StreamCallbackAdapter(ResourceScope resourceScope, IStreamListener streamListener)
    {
        if(resourceScope == null)
        {
            throw new IllegalArgumentException("Resource scope must be non-null");
        }

        if(streamListener == null)
        {
            throw new IllegalArgumentException("Stream listener must be non-null");
        }

        mResourceScope = resourceScope;
        mStreamListener = streamListener;
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
        MemorySegment iSegment = iSamplesPointer.asSegment(CLinker.C_SHORT.byteSize() * sampleCount, mResourceScope);
        MemorySegment qSegment = qSamplesPointer.asSegment(CLinker.C_SHORT.byteSize() * sampleCount, mResourceScope);

        //Copy the I/Q samples from foreign memory to native java arrays
        short[] i = new short[sampleCount];
        short[] q = new short[sampleCount];

        for(int x = 0; x < sampleCount; x++)
        {
            i[x] = MemoryAccess.getShortAtIndex(iSegment, x);
            q[x] = MemoryAccess.getShortAtIndex(qSegment, x);
        }

        //Translate the callback parameters pointer to a memory segment and re-construct the parameters as a Java object
        StreamCallbackParameters parameters = new StreamCallbackParameters(parametersPointer
                .asSegment(sdrplay_api_StreamCbParamsT.sizeof(), mResourceScope));

        mStreamListener.processStreamA(i, q, parameters, Flag.evaluate(reset), deviceContext);
    }
}
