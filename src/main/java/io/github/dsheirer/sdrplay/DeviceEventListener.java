package io.github.dsheirer.sdrplay;

/**
 * Receives events from a streaming device.
 *
 * <p>Runs on a thread of the SDRplay API, so it should return quickly. It must
 * not call {@link RspDevice#stop()} or {@link RspDevice#close()}: on
 * {@link DeviceEvent.DeviceRemoved}, close the device from another thread.
 * Power overload events are acknowledged to the API before they are delivered,
 * as the API requires.
 */
@FunctionalInterface
public interface DeviceEventListener {
    /** Handles an event. */
    void onEvent(DeviceEvent event);
}
