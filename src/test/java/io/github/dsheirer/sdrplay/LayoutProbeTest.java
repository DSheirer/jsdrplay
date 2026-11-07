package io.github.dsheirer.sdrplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.dsheirer.sdrplay.internal.EventType;
import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.OverloadType;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.io.IOException;
import java.io.Reader;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.PaddingLayout;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Compares the layouts and constants in the Java code with what this
 * platform's C compiler produces from the SDRplay API headers, as recorded by
 * {@code native/probe-layouts.sh}. Skipped when that has not been run, except
 * in CI, which sets {@code -PrequireLayoutProbe}.
 */
class LayoutProbeTest {
    private static final List<Class<? extends Enum<?>>> ENUMS = List.of(SDRplayError.class, RspModel.class,
        Tuner.class, RspDuoMode.class, TransferMode.class, Bandwidth.class, IfMode.class, LoMode.class,
        MinGainReduction.class, AgcMode.class, AdsbMode.class, DebugLevel.class, Rsp2Antenna.class,
        Rsp2AmPort.class, RspDuoAmPort.class, RspDxAntenna.class, RspDxHdrBandwidth.class, RspDuoModeChange.class,
        EventType.class, OverloadType.class, UpdateReason.class);

    private static final Map<String, Long> MACROS = Map.ofEntries(
        Map.entry("SDRPLAY_MAX_DEVICES", (long) Layouts.MAX_DEVICES),
        Map.entry("SDRPLAY_MAX_TUNERS_PER_DEVICE", (long) Layouts.MAX_TUNERS_PER_DEVICE),
        Map.entry("SDRPLAY_MAX_SER_NO_LEN", (long) Layouts.MAX_SER_NO_LEN),
        Map.entry("SDRPLAY_MAX_ROOT_NM_LEN", (long) Layouts.MAX_ROOT_NM_LEN),
        Map.entry("MAX_BB_GR", (long) Layouts.MAX_BB_GR));

    private static Properties probe;

    @BeforeAll
    static void loadProbe() throws IOException {
        Path file = Path.of(System.getProperty("jsdrplay.layout.file", "build/sdrplay-api/layout.properties"));
        if (!Files.isRegularFile(file)) {
            if (Boolean.getBoolean("jsdrplay.layout.required")) {
                fail("Layout probe output " + file + " is missing; run native/probe-layouts.sh");
            }
            Assumptions.abort("Run native/probe-layouts.sh to check the layouts against the C compiler");
        }
        probe = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            probe.load(reader);
        }
    }

    @Test
    void probeIsForThePinnedApiVersion() {
        assertEquals(String.format(Locale.ROOT, "%.2f", SDRplay.API_VERSION), probe.getProperty("api.version"));
    }

    @Test
    void structureSizesAndOffsetsMatchTheCompiler() {
        List<String> problems = new ArrayList<>();
        Map<String, String> expected = new TreeMap<>();
        for (Map.Entry<String, GroupLayout> entry : Layouts.ALL.entrySet()) {
            GroupLayout layout = entry.getValue();
            expected.put("struct." + entry.getKey(), Long.toString(layout.byteSize()));
            for (MemoryLayout member : layout.memberLayouts()) {
                if (!(member instanceof PaddingLayout)) {
                    String name = member.name().orElseThrow();
                    expected.put("field." + entry.getKey() + "." + name, Long.toString(Layouts.offset(layout, name)));
                }
            }
        }
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String actual = probe.getProperty(entry.getKey());
            if (actual == null) {
                problems.add(entry.getKey() + " is not in the probe; add it to native/layout-probe.c");
            } else if (!actual.equals(entry.getValue())) {
                problems.add(entry.getKey() + ": Java " + entry.getValue() + ", C " + actual);
            }
        }
        for (String key : probe.stringPropertyNames()) {
            if ((key.startsWith("struct.") || key.startsWith("field.")) && !expected.containsKey(key)) {
                problems.add(key + " is in the probe but not in Layouts.java");
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void enumValuesAndConstantsMatchTheHeaders() throws ReflectiveOperationException {
        Map<String, Long> java = new TreeMap<>(MACROS);
        for (Class<? extends Enum<?>> type : ENUMS) {
            Method code = type.getMethod("code");
            Method nativeName = type.getMethod("nativeName");
            for (Enum<?> constant : type.getEnumConstants()) {
                String name = (String) nativeName.invoke(constant);
                if (name != null) {
                    java.put(name, (long) (int) code.invoke(constant));
                }
            }
        }
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, Long> entry : java.entrySet()) {
            String actual = probe.getProperty("const." + entry.getKey());
            if (actual == null) {
                problems.add(entry.getKey() + " is not in the probe; add it to native/layout-probe.c");
            } else if ((Long.parseLong(actual) & 0xFFFF_FFFFL) != (entry.getValue() & 0xFFFF_FFFFL)) {
                problems.add(entry.getKey() + ": Java " + entry.getValue() + ", C " + actual);
            }
        }
        for (String key : probe.stringPropertyNames()) {
            if (key.startsWith("const.") && !java.containsKey(key.substring("const.".length()))) {
                problems.add(key + " is in the probe but no Java enum or constant has it");
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }
}
