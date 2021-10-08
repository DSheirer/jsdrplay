package io.github.dsheirer.sdrplay.device;

import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

/**
 * Interface for parsing and accessing the fields of the sdrplay_api_DeviceT structure across
 * various versions.
 */
public interface IDeviceStruct
{
    /**
     * Foreign memory segment for this device structure
     */
    MemorySegment getDeviceMemorySegment();

    /**
     * Serial number of the device
     */
    String getSerialNumber();

    /**
     * Indicates the device type, or model of RSP
     */
    DeviceType getDeviceType();

    /**
     * Indicates single, dual, or master/slave mode for the tuner.
     */
    TunerSelect getTunerSelect();

    /**
     * RSPduo mode
     */
    RspDuoMode getRspDuoMode();

    /**
     * Indicates if the device is valid and ready for use (V3.08 and later)
     */
    boolean isValid();

    /**
     * Sample frequency/rate for the RSPduo when in master/slave mode
     */
    double getRspDuoSampleFrequency();

    /**
     * Device handle.  Note this is only available if the device has been selected.
     */
    MemoryAddress getDeviceHandle();
}
