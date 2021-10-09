package io.github.dsheirer.sdrplay.parameter.composite;

import io.github.dsheirer.sdrplay.device.DeviceType;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.RspDuoDeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.RspDuoTunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.TunerParametersFactory;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceParamsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RxChannelParamsT;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSPduo Composite parameters (device and two tuners)
 */
public class RspDuoCompositeParameters extends CompositeParameters<RspDuoDeviceParameters, RspDuoTunerParameters>
{
    private RspDuoTunerParameters mTunerParameters2;
    private ControlParameters mControlParameters2;

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
        MemorySegment memorySegmentRxB = memoryAddressRxB.asSegment(sdrplay_api_RxChannelParamsT.sizeof(), resourceScope);
        mTunerParameters2 = (RspDuoTunerParameters) TunerParametersFactory.create(DeviceType.RSPduo, memorySegmentRxB);

        MemorySegment tuner2ControlParametersMemorySegment = sdrplay_api_RxChannelParamsT.ctrlParams$slice(memorySegmentRxB);
        mControlParameters2 = new ControlParameters(tuner2ControlParametersMemorySegment);
    }

    /**
     * Tuner 2 Tuner Parameters
     */
    public RspDuoTunerParameters getTunerParameters2()
    {
        return mTunerParameters2;
    }

    /**
     * Tuner 2 Control Parameters
     */
    public ControlParameters getControlParameters2()
    {
        return mControlParameters2;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Device Composite Parameters\n");
        sb.append("\tDevice Parameters:\n").append(getDeviceParameters()).append("\n");
        sb.append("\tRX Channel A:\n").append(getTunerParameters1()).append("\n");
        sb.append("\tRX Channel B:\n").append(getTunerParameters2()).append("\n");
        return sb.toString();
    }
}
