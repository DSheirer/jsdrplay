package io.github.dsheirer.sdrplay.parameter.composite;

import io.github.dsheirer.sdrplay.device.DeviceType;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.TunerParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.TunerParametersFactory;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceParamsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RxChannelParamsT;
import io.github.dsheirer.sdrplay.parameter.device.DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.device.DeviceParametersFactory;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Composite Device Parameters structure (sdrplay_api_DeviceParamsT) providing access to the device parameters and
 * the tuner 1 parameters.  Tuner 2 parameters are only accessible via the RSPduo sub-class implementation.
 *
 * Note: sub-class implementations will constrain access to the appropriate sub-structures of the DeviceParamsT
 * structure for each specific device.
 */
public class CompositeParameters<D extends DeviceParameters, T extends TunerParameters>
{
    private D mDeviceParameters;
    private T mTunerParameters1;
    private ControlParameters mControlParameters1;

    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param deviceType to create
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public CompositeParameters(DeviceType deviceType, MemorySegment memorySegment, ResourceScope resourceScope)
    {
        MemoryAddress memoryAddress = sdrplay_api_DeviceParamsT.devParams$get(memorySegment);
        MemorySegment parametersMemorySegment = memoryAddress.asSegment(sdrplay_api_DevParamsT.sizeof(), resourceScope);
        mDeviceParameters = (D) DeviceParametersFactory.create(deviceType, parametersMemorySegment);

        MemoryAddress memoryAddressRx1 = sdrplay_api_DeviceParamsT.rxChannelA$get(memorySegment);
        MemorySegment memorySegmentRx1 = memoryAddressRx1.asSegment(sdrplay_api_RxChannelParamsT.sizeof(), resourceScope);
        mTunerParameters1 = (T) TunerParametersFactory.create(deviceType, memorySegmentRx1);

        MemorySegment tuner1ControlParametersMemorySegment = sdrplay_api_RxChannelParamsT.ctrlParams$slice(memorySegmentRx1);
        mControlParameters1 = new ControlParameters(tuner1ControlParametersMemorySegment);
    }

    /**
     * Device parameters
     */
    public D getDeviceParameters()
    {
        return mDeviceParameters;
    }

    /**
     * Tuner 1 Parameters
     */
    public T getTunerParameters1()
    {
        return mTunerParameters1;
    }

    /**
     * Tuner 1 Control Parameters
     */
    public ControlParameters getControlParameters1()
    {
        return mControlParameters1;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Composite Parameters\n");
        sb.append("\tDevice Parameters:\n").append(getDeviceParameters()).append("\n");
        sb.append("\tTuner 1 Parameters:\n").append(getTunerParameters1()).append("\n");
        return sb.toString();
    }
}
