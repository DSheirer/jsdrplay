package io.github.dsheirer.sdrplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Receive-only tests against connected devices. Enable with
 * {@code -PhardwareTest=true}. The bias tee is never turned on.
 */
@EnabledIfSystemProperty(named = "jsdrplay.hardware.test", matches = "true")
class HardwareTest {
    private static final double FREQUENCY = 100_000_000;
    private static SDRplay sdrplay;

    @BeforeAll
    static void open() throws SDRplayException {
        sdrplay = SDRplay.open();
        assertFalse(sdrplay.devices().isEmpty(), "No SDRplay device found");
    }

    @AfterAll
    static void close() throws SDRplayException {
        if (sdrplay != null) {
            sdrplay.close();
        }
    }

    /** Selects a single-tuner model if there is one, since it has the fewest special cases. */
    private static RspDevice selectAny() throws SDRplayException {
        List<DeviceInfo> devices = sdrplay.devices();
        DeviceInfo choice = devices.stream().filter(d -> d.model() != RspModel.RSPDUO).findFirst()
            .orElse(devices.getFirst());
        return sdrplay.select(choice);
    }

    private static void configure(RspDevice device) throws SDRplayException {
        device.setSampleRate(2_000_000);
        RxChannel channel = device.channel();
        channel.setDecimation(1, false);
        channel.setBandwidth(Bandwidth.KHZ_1536);
        channel.setIfMode(IfMode.ZERO);
        channel.setFrequency(FREQUENCY);
        channel.setAgc(AgcMode.DISABLED);
        channel.setGain(40, 0);
    }

    @Test
    void storesSettingsBeforeStreaming() throws SDRplayException {
        for (DeviceInfo info : sdrplay.devices()) {
            try (RspDevice device = sdrplay.select(info)) {
                assertEquals(info.serialNumber(), device.serialNumber());
                configure(device);
                RxChannel channel = device.channel();
                assertEquals(2_000_000, device.sampleRate());
                assertEquals(FREQUENCY, channel.frequency());
                assertEquals(40, channel.gainReduction());
                assertEquals(0, channel.lnaState());
                assertEquals(Bandwidth.KHZ_1536, channel.bandwidth());
                assertEquals(IfMode.ZERO, channel.ifMode());
                assertEquals(AgcMode.DISABLED, channel.agcMode());
                assertEquals(1, channel.decimationFactor());
                channel.setDecimation(4, true);
                assertEquals(4, channel.decimationFactor());
                channel.setAgc(AgcMode.HZ_50, -30);
                assertEquals(-30, channel.agcSetPoint());
                device.setPpm(0.5);
                assertEquals(0.5, device.ppm());
                assertEquals(TransferMode.ISOCHRONOUS, device.transferMode());
                assertTrue(channel.isDcOffsetCorrection());
                assertThrows(IllegalArgumentException.class, () -> channel.setDecimation(3, false));
                assertThrows(IllegalArgumentException.class, () -> channel.setGainReduction(60));
                if (device instanceof Rsp1aDevice rsp1a) {
                    rsp1a.setRfNotch(true);
                    assertTrue(rsp1a.isRfNotch());
                    assertFalse(rsp1a.isBiasT());
                } else if (device instanceof RspDuoDevice duo) {
                    assertEquals(RspDuoMode.SINGLE_TUNER, duo.mode());
                    duo.setRfNotch(duo.tuner(), true);
                    assertTrue(duo.isRfNotch(duo.tuner()));
                }
            }
        }
    }

    @Test
    void streamsSamplesAndStops() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            CountDownLatch received = new CountDownLatch(50);
            AtomicReference<short[]> last = new AtomicReference<>();
            AtomicInteger counts = new AtomicInteger();
            device.start(buffer -> {
                counts.set(buffer.sampleCount());
                last.set(buffer.copyInterleaved());
                received.countDown();
            });
            assertTrue(received.await(10, TimeUnit.SECONDS), "Samples did not arrive");
            assertTrue(device.isStreaming());
            device.stop();
            assertFalse(device.isStreaming());
            assertNull(device.listenerFailure());
            short[] samples = last.get();
            assertEquals(counts.get() * 2, samples.length);
            boolean nonZero = false;
            for (short sample : samples) {
                nonZero |= sample != 0;
            }
            assertTrue(nonZero, "Samples were all zero");
        }
    }

    @Test
    void updatesSettingsWhileStreaming() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            AtomicInteger frequencyChanges = new AtomicInteger();
            AtomicInteger gainChanges = new AtomicInteger();
            CountDownLatch started = new CountDownLatch(10);
            device.start(buffer -> {
                if (buffer.frequencyChanged()) {
                    frequencyChanges.incrementAndGet();
                }
                if (buffer.gainChanged()) {
                    gainChanges.incrementAndGet();
                }
                started.countDown();
            });
            assertTrue(started.await(10, TimeUnit.SECONDS), "Samples did not arrive");
            RxChannel channel = device.channel();
            // Back-to-back changes of one kind must wait for each other rather than fail.
            for (int step = 1; step <= 5; step++) {
                channel.setFrequency(FREQUENCY + step * 1_000_000);
                channel.setGainReduction(30 + step);
            }
            channel.setBandwidth(Bandwidth.KHZ_600);
            channel.setAgc(AgcMode.HZ_5);
            channel.setAgc(AgcMode.DISABLED);
            device.setPpm(1.0);
            for (int i = 0; i < 100 && frequencyChanges.get() < 5; i++) {
                Thread.sleep(10);
            }
            assertEquals(FREQUENCY + 5_000_000, channel.frequency());
            assertTrue(frequencyChanges.get() >= 1, "No frequency change was reported");
            assertTrue(gainChanges.get() >= 1, "No gain change was reported");
            assertNull(device.listenerFailure());
        }
    }

    @Test
    void restarts() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            for (int run = 0; run < 3; run++) {
                CountDownLatch received = new CountDownLatch(5);
                device.start(buffer -> received.countDown());
                assertTrue(received.await(10, TimeUnit.SECONDS), "No samples in run " + run);
                device.stop();
            }
            assertThrows(IllegalStateException.class, () -> {
                device.start(buffer -> { });
                device.start(buffer -> { });
            });
        }
    }

    @Test
    void listenerExceptionStopsDelivery() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            AtomicInteger calls = new AtomicInteger();
            device.start(buffer -> {
                calls.incrementAndGet();
                throw new IllegalArgumentException("expected");
            });
            for (int i = 0; i < 500 && device.listenerFailure() == null; i++) {
                Thread.sleep(10);
            }
            assertInstanceOf(IllegalArgumentException.class, device.listenerFailure());
            Thread.sleep(200);
            assertEquals(1, calls.get());
            device.stop();
        }
    }

    @Test
    void samplesCannotBeUsedAfterCallbackReturns() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            AtomicReference<MemorySegment> kept = new AtomicReference<>();
            CountDownLatch called = new CountDownLatch(1);
            device.start(buffer -> {
                kept.compareAndSet(null, buffer.i());
                called.countDown();
            });
            assertTrue(called.await(10, TimeUnit.SECONDS));
            device.stop();
            // WrongThreadException here, as this is not the callback's thread.
            assertThrows(RuntimeException.class, () -> kept.get().get(ValueLayout.JAVA_SHORT, 0));
        }
    }

    @Test
    @SuppressWarnings("try")
    void stoppingFromListenerIsRejected() throws Exception {
        try (RspDevice device = selectAny()) {
            configure(device);
            AtomicReference<Throwable> thrown = new AtomicReference<>();
            AtomicBoolean first = new AtomicBoolean(true);
            CountDownLatch called = new CountDownLatch(1);
            device.start(buffer -> {
                if (first.getAndSet(false)) {
                    try {
                        device.close();
                    } catch (Throwable t) {
                        thrown.set(t);
                    }
                    called.countDown();
                }
            });
            assertTrue(called.await(10, TimeUnit.SECONDS));
            assertInstanceOf(IllegalStateException.class, thrown.get());
        }
    }

    @Test
    void closedDeviceIsRejected() throws SDRplayException {
        RspDevice device = selectAny();
        device.close();
        device.close();
        assertTrue(device.isClosed());
        assertThrows(IllegalStateException.class, device::sampleRate);
        assertThrows(IllegalStateException.class, () -> device.channel().setFrequency(FREQUENCY));
    }

    @Test
    void rspDuoStreamsBothTunersInDualTunerMode() throws Exception {
        DeviceInfo duo = sdrplay.devices().stream()
            .filter(d -> d.rspDuoModes().contains(RspDuoMode.DUAL_TUNER)).findFirst().orElse(null);
        Assumptions.assumeTrue(duo != null, "No RSPduo available for dual tuner mode");
        try (RspDuoDevice device = sdrplay.selectRspDuo(duo, RspDuoMode.DUAL_TUNER, Tuner.BOTH, 0)) {
            assertEquals(2, device.channels().size());
            for (RxChannel channel : device.channels()) {
                channel.setFrequency(FREQUENCY);
                channel.setAgc(AgcMode.DISABLED);
                channel.setGain(40, 0);
            }
            device.channel(Tuner.B).setFrequency(FREQUENCY + 1_000_000);
            Set<Tuner> seen = ConcurrentHashMap.newKeySet();
            CountDownLatch received = new CountDownLatch(40);
            device.start(buffer -> {
                seen.add(buffer.tuner());
                received.countDown();
            });
            assertTrue(received.await(10, TimeUnit.SECONDS), "Samples did not arrive");
            device.channel(Tuner.B).setFrequency(FREQUENCY + 2_000_000);
            device.stop();
            assertEquals(EnumSet.of(Tuner.A, Tuner.B), seen);
            assertNull(device.listenerFailure());
            assertThrows(IllegalArgumentException.class, () -> sdrplay.selectRspDuo(duo, RspDuoMode.DUAL_TUNER,
                Tuner.A, 0));
        }
    }

    @Test
    void rspDuoSelectsTunerB() throws Exception {
        DeviceInfo duo = sdrplay.devices().stream()
            .filter(d -> d.rspDuoModes().contains(RspDuoMode.SINGLE_TUNER) && d.tuners().contains(Tuner.B))
            .findFirst().orElse(null);
        Assumptions.assumeTrue(duo != null, "No RSPduo with a free tuner B");
        try (RspDuoDevice device = sdrplay.selectRspDuo(duo, RspDuoMode.SINGLE_TUNER, Tuner.B, 0)) {
            assertEquals(Tuner.B, device.channel().tuner());
            assertThrows(IllegalArgumentException.class, () -> device.channel(Tuner.A));
            device.setSampleRate(2_000_000);
            device.channel().setFrequency(FREQUENCY);
            CountDownLatch received = new CountDownLatch(10);
            Set<Tuner> seen = ConcurrentHashMap.newKeySet();
            device.start(buffer -> {
                seen.add(buffer.tuner());
                received.countDown();
            });
            assertTrue(received.await(10, TimeUnit.SECONDS), "Samples did not arrive");
            device.stop();
            assertEquals(Set.of(Tuner.B), seen);
        }
    }
}
