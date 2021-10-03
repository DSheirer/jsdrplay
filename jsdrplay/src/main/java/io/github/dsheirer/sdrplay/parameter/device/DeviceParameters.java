package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.api.sdrplay_api_DevParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Device Parameters structure (sdrplay_api_DevParamsT)
 */
public abstract class DeviceParameters
{
    private MemorySegment mMemorySegment;
    private SamplingFrequency mSamplingFrequency;
    private SynchronousUpdate mSynchronousUpdate;
    private ResetFlags mResetFlags;

    public DeviceParameters(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
        mSamplingFrequency = new SamplingFrequency(sdrplay_api_DevParamsT.fsFreq$slice(memorySegment));
        mSynchronousUpdate = new SynchronousUpdate(sdrplay_api_DevParamsT.syncUpdate$slice(memorySegment));
        mResetFlags = new ResetFlags(sdrplay_api_DevParamsT.resetFlags$slice(memorySegment));
    }

    /**
     * Foreign memory segment for this device parameters instance
     */
    protected MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Parts Per Million (ppm) center frequency correction value.
     * @return ppm
     */
    public double getPPM()
    {
        return sdrplay_api_DevParamsT.ppm$get(getMemorySegment());
    }

    /**
     * Sets the parts per million (ppm) center frequency correction value
     * @param ppm parts per million
     */
    public void setPPM(double ppm)
    {
        sdrplay_api_DevParamsT.ppm$set(getMemorySegment(), ppm);
    }

    /**
     * Sampling frequency structure.
     */
    public SamplingFrequency getSamplingFrequency()
    {
        return mSamplingFrequency;
    }

    /**
     * Synchronous update structure
     */
    public SynchronousUpdate getSynchronousUpdate()
    {
        return mSynchronousUpdate;
    }

    /**
     * Reset request flags
     */
    public ResetFlags getResetFlags()
    {
        return mResetFlags;
    }

    /**
     * USB Transfer mode currently used by the device
     */
    public TransferMode getTransferMode()
    {
        return TransferMode.fromValue(sdrplay_api_DevParamsT.mode$get(getMemorySegment()));
    }

    /**
     * Sets the USB transfer mode used by the device
     */
    public void setTransferMode(TransferMode transferMode)
    {
        if(transferMode != TransferMode.UNKNOWN)
        {
            sdrplay_api_DevParamsT.mode$set(getMemorySegment(), transferMode.getValue());
        }
    }

    public long getSamplesPerPacket()
    {
        return sdrplay_api_DevParamsT.samplesPerPkt$get(getMemorySegment());
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\tDevice Parameters").append("\n");
        sb.append("\t\tPPM: ").append(getPPM()).append("\n");
        sb.append("\t\tSample Rate: ").append(getSamplingFrequency()).append("\n");
        sb.append("\t\tSamples Per Packet: ").append(getSamplesPerPacket()).append("\n");
        sb.append("\t\tSync Update: ").append(getSynchronousUpdate()).append("\n");
        sb.append("\t\tReset Flags: ").append(getResetFlags()).append("\n");
        sb.append("\t\tTransfer Mode: ").append(getTransferMode()).append("\n");

        return sb.toString();
    }
}
