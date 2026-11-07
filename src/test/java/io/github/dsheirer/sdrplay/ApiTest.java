package io.github.dsheirer.sdrplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * Tests against the installed SDRplay API and its service, without selecting
 * a device. Skipped where the API is not installed, as on CI machines, which
 * instead check that its absence is reported clearly.
 */
class ApiTest {
    static boolean installed() {
        return SDRplay.isInstalled();
    }

    @Test
    @DisabledIf("installed")
    void reportsMissingApi() {
        SDRplayException e = assertThrows(SDRplayException.class, SDRplay::open);
        assertEquals(SDRplayError.API_NOT_FOUND, e.error());
        assertTrue(e.getMessage().contains("https://www.sdrplay.com/api/"), e.getMessage());
        assertFalse(SDRplay.isInstalled());
    }

    @Test
    @EnabledIf("installed")
    void opensNestedSessions() throws SDRplayException {
        try (SDRplay outer = SDRplay.open()) {
            assertTrue(SDRplay.isSupportedVersion(outer.apiVersion()), "API " + outer.apiVersion());
            assertNotNull(outer.location());
            try (SDRplay inner = SDRplay.open()) {
                assertEquals(outer.apiVersion(), inner.apiVersion());
            }
            // Closing the inner session must leave the connection open for the outer one.
            assertNotNull(outer.devices());
        }
        try (SDRplay again = SDRplay.open()) {
            assertNotNull(again.devices());
        }
    }

    @Test
    @EnabledIf("installed")
    void listsDevices() throws SDRplayException {
        try (SDRplay sdrplay = SDRplay.open()) {
            List<DeviceInfo> devices = sdrplay.devices();
            for (DeviceInfo device : devices) {
                assertFalse(device.serialNumber().isBlank(), device.toString());
                assertFalse(device.tuners().isEmpty(), device.toString());
                assertEquals(device.model() == RspModel.RSPDUO, !device.rspDuoModes().isEmpty(), device.toString());
            }
        }
    }

    @Test
    @EnabledIf("installed")
    void closedSessionIsRejected() throws SDRplayException {
        SDRplay sdrplay = SDRplay.open();
        sdrplay.close();
        sdrplay.close();
        assertThrows(IllegalStateException.class, sdrplay::devices);
        assertThrows(IllegalArgumentException.class, () -> {
            try (SDRplay other = SDRplay.open()) {
                other.select("no-such-serial");
            }
        });
    }
}
