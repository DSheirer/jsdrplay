package io.github.dsheirer.sdrplay;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.NativeLibraryLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that need neither the SDRplay API nor a device. */
class LibraryTest {
    @Test
    void structureSizesMatchTheHeaders() {
        // Sizes from layout-probe on Linux x86-64; LayoutProbeTest checks every platform in CI.
        assertEquals(96, Layouts.DEVICE.byteSize());
        assertEquals(88, Layouts.offset(Layouts.DEVICE, "dev"));
        assertEquals(64, Layouts.DEV_PARAMS.byteSize());
        assertEquals(144, Layouts.RX_CHANNEL_PARAMS.byteSize());
        assertEquals(140, Layouts.offset(Layouts.RX_CHANNEL_PARAMS, "rspDxTunerParams"));
        assertEquals(1540, Layouts.ERROR_INFO.byteSize());
        assertEquals(16, Layouts.EVENT_PARAMS.byteSize());
        assertEquals(20, Layouts.STREAM_CB_PARAMS.byteSize());
    }

    @Test
    void acceptsApi315AndLater3x() {
        assertTrue(SDRplay.isSupportedVersion(3.15f));
        assertTrue(SDRplay.isSupportedVersion(3.16f));
        assertTrue(SDRplay.isSupportedVersion(3.99f));
        assertFalse(SDRplay.isSupportedVersion(3.14f));
        assertFalse(SDRplay.isSupportedVersion(3.07f));
        assertFalse(SDRplay.isSupportedVersion(4.0f));
        SDRplayException e = assertThrows(SDRplayException.class, () -> SDRplay.checkVersion(3.08f));
        assertEquals(SDRplayError.UNSUPPORTED_API_VERSION, e.error());
    }

    @Test
    void decodesErrors() {
        assertEquals(SDRplayError.SERVICE_NOT_RESPONDING, SDRplayError.fromCode(14));
        assertEquals(SDRplayError.INVALID_SERVICE_VERSION, SDRplayError.fromCode(24));
        assertEquals(SDRplayError.OTHER, SDRplayError.fromCode(12345));
        assertEquals("sdrplay_api_Init failed: sdrplay_api_HwError (7): hardware",
            new SDRplayException("sdrplay_api_Init", 7, "hardware").getMessage());
        assertEquals("sdrplay_api_Init failed: unknown error (99)",
            new SDRplayException("sdrplay_api_Init", 99, null).getMessage());
    }

    @Test
    void decodesEnums() {
        assertEquals(RspModel.RSPDX_R2, RspModel.fromCode(7));
        assertEquals(RspModel.RSP1A, RspModel.fromCode(255));
        assertEquals(RspModel.UNKNOWN, RspModel.fromCode(42));
        assertEquals(Set.of(Tuner.A, Tuner.B), Tuner.fromMask(3));
        assertEquals(Set.of(Tuner.B), Tuner.fromMask(2));
        assertEquals(Set.of(RspDuoMode.SINGLE_TUNER, RspDuoMode.DUAL_TUNER, RspDuoMode.MASTER),
            RspDuoMode.fromMask(7));
        assertEquals(Set.of(RspDuoMode.SLAVE), RspDuoMode.fromMask(8));
        assertEquals(Bandwidth.UNDEFINED, Bandwidth.fromCode(1234));
        assertEquals(1_536_000, Bandwidth.KHZ_1536.hertz());
        assertEquals(IfMode.KHZ_2048, IfMode.fromCode(2048));
        assertThrows(IllegalArgumentException.class, () -> AgcMode.fromCode(99));
    }

    @Test
    void describesDeviceEntries() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment entry = arena.allocate(Layouts.DEVICE);
            byte[] serial = "1905039A32".getBytes(StandardCharsets.US_ASCII);
            MemorySegment.copy(serial, 0, entry, ValueLayout.JAVA_BYTE, 0, serial.length);
            entry.set(ValueLayout.JAVA_BYTE, Layouts.offset(Layouts.DEVICE, "hwVer"), (byte) 3);
            entry.set(ValueLayout.JAVA_INT, Layouts.offset(Layouts.DEVICE, "tuner"), 2);
            entry.set(ValueLayout.JAVA_INT, Layouts.offset(Layouts.DEVICE, "rspDuoMode"), 8);
            entry.set(ValueLayout.JAVA_DOUBLE, Layouts.offset(Layouts.DEVICE, "rspDuoSampleFreq"), 6e6);
            entry.set(ValueLayout.JAVA_BYTE, Layouts.offset(Layouts.DEVICE, "valid"), (byte) 1);
            DeviceInfo info = SDRplay.describe(entry);
            assertEquals(new DeviceInfo("1905039A32", RspModel.RSPDUO, 3, Set.of(Tuner.B), Set.of(RspDuoMode.SLAVE),
                6e6, true), info);

            entry.set(ValueLayout.JAVA_BYTE, Layouts.offset(Layouts.DEVICE, "hwVer"), (byte) 255);
            DeviceInfo rsp1a = SDRplay.describe(entry);
            assertEquals(RspModel.RSP1A, rsp1a.model());
            assertEquals(Set.of(Tuner.A), rsp1a.tuners());
            assertTrue(rsp1a.rspDuoModes().isEmpty());
        }
    }

    @Test
    void copiesSamples() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment i = arena.allocateFrom(ValueLayout.JAVA_SHORT, (short) 16384, (short) -32768);
            MemorySegment q = arena.allocateFrom(ValueLayout.JAVA_SHORT, (short) -16384, (short) 0);
            SampleBuffer buffer = new SampleBuffer(Tuner.A, i, q, 2, 7, false, false, false, false);
            assertArrayEquals(new short[] {16384, -32768}, buffer.copyI());
            assertArrayEquals(new short[] {-16384, 0}, buffer.copyQ());
            assertArrayEquals(new short[] {16384, -16384, -32768, 0}, buffer.copyInterleaved());
            assertArrayEquals(new float[] {0.5f, -0.5f, -1f, 0f}, buffer.copyInterleavedFloats());
            float[] target = new float[6];
            buffer.copyInterleavedFloats(target);
            assertArrayEquals(new float[] {0.5f, -0.5f, -1f, 0f, 0f, 0f}, target);
            assertThrows(IndexOutOfBoundsException.class, () -> buffer.copyInterleaved(new short[3]));
        }
    }

    @Test
    void mapsPlatforms() {
        assertEquals("linux-x86_64", NativeLibraryLoader.platform("Linux", "amd64"));
        assertEquals("linux-aarch64", NativeLibraryLoader.platform("Linux", "aarch64"));
        assertEquals("macos-x86_64", NativeLibraryLoader.platform("Mac OS X", "x86_64"));
        assertEquals("macos-aarch64", NativeLibraryLoader.platform("Mac OS X", "aarch64"));
        assertEquals("windows-x86_64", NativeLibraryLoader.platform("Windows 11", "amd64"));
        assertEquals("windows-aarch64", NativeLibraryLoader.platform("Windows 11", "aarch64"));
        assertThrows(UnsatisfiedLinkError.class, () -> NativeLibraryLoader.platform("Solaris", "amd64"));
        assertThrows(UnsatisfiedLinkError.class, () -> NativeLibraryLoader.platform("Linux", "riscv64"));
    }

    @Test
    void looksInTheInstallerLocationsFirst() {
        List<String> linux = NativeLibraryLoader.candidates("linux-x86_64", null, Map.of());
        assertEquals("/usr/local/lib/libsdrplay_api.so.3", linux.getFirst());
        assertTrue(linux.contains("/usr/lib/x86_64-linux-gnu/libsdrplay_api.so.3"));
        assertEquals("libsdrplay_api.so", linux.getLast());

        List<String> mac = NativeLibraryLoader.candidates("macos-aarch64", null, Map.of());
        assertEquals("/usr/local/lib/libsdrplay_api.so.3", mac.getFirst());

        List<String> windows = NativeLibraryLoader.candidates("windows-x86_64", null,
            Map.of("ProgramFiles", "D:\\Programs"));
        assertEquals(List.of("D:\\Programs\\SDRplay\\API\\x64\\sdrplay_api.dll", "sdrplay_api.dll"), windows);

        List<String> windowsArm = NativeLibraryLoader.candidates("windows-aarch64", null, Map.of());
        assertEquals("C:\\Program Files\\SDRplay\\API\\ARM64\\sdrplay_api.dll", windowsArm.getFirst());
    }

    @Test
    void overrideReplacesTheSearch(@TempDir Path directory) throws Exception {
        Path library = Files.createFile(directory.resolve("custom.so"));
        assertEquals(List.of(library.toString()),
            NativeLibraryLoader.candidates("linux-x86_64", library.toString(), Map.of()));
        assertEquals(List.of(directory.resolve("libsdrplay_api.so.3").toString(),
                directory.resolve("libsdrplay_api.so").toString()),
            NativeLibraryLoader.candidates("linux-x86_64", directory.toString(), Map.of()));
    }
}
