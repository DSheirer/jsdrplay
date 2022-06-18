package com.github.dsheirer.sdrplay.parameter.composite;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceParamsT;
import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RxChannelParamsT;
import com.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import com.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.TunerParametersFactory;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSPduo Composite parameters (device and two tuners)
 */
public class RspDuoCompositeParameters extends CompositeParameters<RspDuoDeviceParameters, RspDuoTunerParameters>
{
    private RspDuoTunerParameters mTunerBParameters;
    private ControlParameters mControlBParameters;

    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public RspDuoCompositeParameters(MemorySegment memorySegment, ResourceScope resourceScope)
    {
        super(DeviceType.RSPduo, memorySegment, resourceScope);

        MemoryAddress memoryAddressRxB = sdrplay_api_DeviceParamsT.rxChannelB$get(memorySegment);
        MemorySegment memorySegmentRxB = sdrplay_api_RxChannelParamsT.ofAddress(memoryAddressRxB, resourceScope);
        mTunerBParameters = (RspDuoTunerParameters) TunerParametersFactory.create(DeviceType.RSPduo, memorySegmentRxB);

        MemorySegment tunerBControlParametersMemorySegment = sdrplay_api_RxChannelParamsT.ctrlParams$slice(memorySegmentRxB);
        mControlBParameters = new ControlParameters(tunerBControlParametersMemorySegment);
    }

    /**
     * Tuner B Tuner Parameters
     */
    public RspDuoTunerParameters getTunerBParameters()
    {
        return mTunerBParameters;
    }

    /**
     * Tuner B Control Parameters
     */
    public ControlParameters getControlBParameters()
    {
        return mControlBParameters;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Device Composite Parameters\n");
        sb.append("\tDevice Parameters:\n").append(getDeviceParameters()).append("\n");
        sb.append("\tTuner Channel A:\n").append(getTunerAParameters()).append("\n");
        sb.append("\tTuner Channel B:\n").append(getTunerBParameters()).append("\n");
        return sb.toString();
    }
}
