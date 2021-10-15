package io.github.dsheirer.sdrplay;

import io.github.dsheirer.sdrplay.device.Device;
import io.github.dsheirer.sdrplay.device.DeviceType;

import java.util.Collections;
import java.util.List;

/**
 * Descriptor that provides limited details for an RSP device and the available selection modes.
 */
public class DeviceDescriptor
{
    private Device mDevice;
    private List<DeviceSelectionMode> mDeviceSelectionModes;

    /**
     * Constructs a device descriptor
     * @param device to be described
     * @param deviceSelectionModes that are available for the device
     */
    public DeviceDescriptor(Device device, List<DeviceSelectionMode> deviceSelectionModes)
    {
        mDevice = device;
        mDeviceSelectionModes = deviceSelectionModes;
    }

    /**
     * Device that backs this descriptor.
     *
     * Note: package private.  This method is not intended to be exposed to the library user.
     */
    Device getDevice()
    {
        return mDevice;
    }

    /**
     * Serial number for the device
     */
    public String getSerialNumber()
    {
        return mDevice.getSerialNumber();
    }

    /**
     * Indicates if this device descriptor's serial number matches the specified serial number
     * @param serialNumber to compare
     * @return true if there is a match
     */
    public boolean matches(String serialNumber)
    {
        return getSerialNumber() != null && getSerialNumber().equalsIgnoreCase(serialNumber);
    }

    /**
     * Type of RSP device
     */
    public DeviceType getDeviceType()
    {
        return mDevice.getDeviceType();
    }

    /**
     * List of selection modes available for this device.  Most RSP devices will only be available for selection as
     * as single tuner.  The RSPduo has several selection modes available, depending on how the device is currently
     * being used.
     */
    public List<DeviceSelectionMode> getDeviceSelectionModes()
    {
        return Collections.unmodifiableList(mDeviceSelectionModes);
    }

    /**
     * Indicates if the device supports the device selection mode
     * @param deviceSelectionMode
     * @return true if supported
     */
    public boolean supports(DeviceSelectionMode deviceSelectionMode)
    {
        return mDeviceSelectionModes.contains(deviceSelectionMode);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Device: ").append(getDeviceType());
        sb.append(" Serial #:").append(getSerialNumber());
        sb.append(" Available Selection Modes ").append(getDeviceSelectionModes());
        return sb.toString();
    }
}
