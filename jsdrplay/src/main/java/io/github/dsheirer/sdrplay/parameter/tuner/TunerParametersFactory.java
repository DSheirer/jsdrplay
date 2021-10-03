package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.device.DeviceType;
import io.github.dsheirer.sdrplay.api.sdrplay_api_RxChannelParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Factory for creating tuner parameters structures
 */
public class TunerParametersFactory
{
    /**
     * Creats a tuner parameters instance for the specified device type
     * @param deviceType to create
     * @param memorySegment for the sdrplay_api_RxChannelParamsT structure
     * @return tuner parameters
     */
    public static TunerParameters create(DeviceType deviceType, MemorySegment memorySegment)
    {
        MemorySegment tunerParametersMemorySegment = sdrplay_api_RxChannelParamsT.tunerParams$slice(memorySegment);

        switch(deviceType)
        {
            case RSP1 -> {
                return new Rsp1TunerParameters(tunerParametersMemorySegment);
            }
            case RSP1A -> {
                MemorySegment rsp1AMemorySegment = sdrplay_api_RxChannelParamsT.rsp1aTunerParams$slice(memorySegment);
                return new Rsp1aTunerParameters(memorySegment, rsp1AMemorySegment);
            }
            case RSP2 -> {
                MemorySegment rsp2MemorySegment = sdrplay_api_RxChannelParamsT.rsp2TunerParams$slice(memorySegment);
                return new Rsp2TunerParameters(memorySegment, rsp2MemorySegment);
            }
            case RSPduo -> {
                MemorySegment rspDuoMemorySegment = sdrplay_api_RxChannelParamsT.rspDuoTunerParams$slice(memorySegment);
                return new RspDuoTunerParameters(memorySegment, rspDuoMemorySegment);
            }
            case RSPdx -> {
                MemorySegment rspDxMemorySegment = sdrplay_api_RxChannelParamsT.rspDxTunerParams$slice(memorySegment);
                return new RspDxTunerParameters(memorySegment, rspDxMemorySegment);
            }
            default -> {
                throw new IllegalArgumentException("Unrecognized device type: " + deviceType);
            }
        }
    }
}
