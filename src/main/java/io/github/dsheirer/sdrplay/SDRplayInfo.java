package io.github.dsheirer.sdrplay;

/**
 * Command line tool that reports the installed SDRplay API and the connected
 * devices. It does not select any device, so it is safe to run while another
 * application is using one.
 */
public final class SDRplayInfo {
    private SDRplayInfo() {
    }

    /** Runs the tool. */
    public static void main(String[] args) {
        String version = SDRplayInfo.class.getPackage().getImplementationVersion();
        System.out.println("jsdrplay " + (version == null ? "(development build)" : version)
            + ", for SDRplay API " + SDRplay.API_VERSION + " and later 3.x");
        String location;
        try {
            location = SDRplay.libraryLocation();
        } catch (SDRplayException e) {
            System.out.println("SDRplay API library: not found");
            System.out.println("  " + e.getMessage());
            return;
        }
        System.out.println("SDRplay API library: " + location);

        try (SDRplay sdrplay = SDRplay.open()) {
            System.out.println("SDRplay API version: " + sdrplay.apiVersion());
            var devices = sdrplay.devices();
            System.out.println("Devices found: " + devices.size());
            for (DeviceInfo device : devices) {
                System.out.println("  " + device.model().displayName() + ", serial " + device.serialNumber()
                    + ", hardware version " + device.hardwareVersion());
                if (device.model() == RspModel.RSPDUO) {
                    System.out.println("    free tuners: " + device.tuners() + ", modes: " + device.rspDuoModes());
                }
            }
        } catch (SDRplayException e) {
            // A missing or stopped service, or an unsupported API version, ends up here.
            System.out.println("Unable to use the SDRplay API: " + e.getMessage());
        }
    }
}
