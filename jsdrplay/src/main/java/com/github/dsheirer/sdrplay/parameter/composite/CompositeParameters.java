package com.github.dsheirer.sdrplay.parameter.composite;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceParamsT;
import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.parameter.device.DeviceParameters;
import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RxChannelParamsT;
import com.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import com.github.dsheirer.sdrplay.parameter.device.DeviceParametersFactory;
import com.github.dsheirer.sdrplay.parameter.tuner.TunerParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.TunerParametersFactory;
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
    private T mTunerAParameters;
    private ControlParameters mControlAParameters;

    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param deviceType to create
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public CompositeParameters(DeviceType deviceType, MemorySegment memorySegment, ResourceScope resourceScope)
    {
        MemoryAddress parametersMemoryAddress = sdrplay_api_DeviceParamsT.devParams$get(memorySegment);
        MemorySegment parametersMemorySegment = sdrplay_api_DeviceParamsT.ofAddress(parametersMemoryAddress, resourceScope);
        mDeviceParameters = (D) DeviceParametersFactory.create(deviceType, parametersMemorySegment);

        MemoryAddress memoryAddressRxA = sdrplay_api_DeviceParamsT.rxChannelA$get(memorySegment);
        MemorySegment memorySegmentRxA = sdrplay_api_RxChannelParamsT.ofAddress(memoryAddressRxA, resourceScope);
        mTunerAParameters = (T) TunerParametersFactory.create(deviceType, memorySegmentRxA);

        MemorySegment tunerAControlParametersMemorySegment = sdrplay_api_RxChannelParamsT.ctrlParams$slice(memorySegmentRxA);
        mControlAParameters = new ControlParameters(tunerAControlParametersMemorySegment);
    }

    /**
     * Device parameters
     */
    public D getDeviceParameters()
    {
        return mDeviceParameters;
    }

    /**
     * Tuner A Parameters.
     *
     * Note: this is normally mapped to tuner 1.  In the RSPduo, this is mapped to Tuner 1 or Tuner 2, according to how
     * the user has setup the TunerSelect.
     */
    public T getTunerAParameters()
    {
        return mTunerAParameters;
    }

    /**
     * Tuner A Control Parameters
     *
     * Note: this is normally mapped to tuner 1.  In the RSPduo, this is mapped to Tuner 1 or Tuner 2, according to how
     * the user has setup the TunerSelect.
     */
    public ControlParameters getControlAParameters()
    {
        return mControlAParameters;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Composite Parameters\n");
        sb.append("\tDevice Parameters:\n").append(getDeviceParameters()).append("\n");
        sb.append("\tTuner A Parameters:\n").append(getTunerAParameters()).append("\n");
        return sb.toString();
    }
}
