# jsdrplay

Java bindings for the [SDRplay API](https://www.sdrplay.com/api/), the driver
for the SDRplay RSP1, RSP1A, RSP1B, RSP2, RSPduo, RSPdx and RSPdx-R2
software-defined radios, built on the Java Foreign Function & Memory API.

| OS | x86-64 | aarch64 |
|---------|----|----|
| Linux | ✓ | ✓ |
| macOS | ✓ | ✓ |
| Windows | ✓ | ✓ |

Requires Java 23 or later, and SDRplay API 3.15 or a later 3.x version.

## The SDRplay API is installed separately

The jar does **not** contain the native library. The SDRplay API is closed source, 
so it cannot be built from source, and its licence does not allow it to be 
redistributed. It also needs a system service, which only SDRplay's installer 
sets up, to reach the devices. Install it from <https://www.sdrplay.com/api/>; 
jsdrplay finds theinstalled library at run time.

The jar is the same on every platform. Instead of a native build, CI checks the
Java bindings on each of the six platforms against the C headers of the pinned
API release; see [Building](#building).

## Getting it

Download `jsdrplay-<version>.jar` from
[Releases](https://github.com/DSheirer/jsdrplay/releases). The rolling
[`nightly`](https://github.com/DSheirer/jsdrplay/releases/tag/nightly)
pre-release tracks `master`.

Check that your machine can see the API and the devices:

```
java -jar jsdrplay-<version>.jar
```

```
jsdrplay 3.15.0, for SDRplay API 3.15 and later 3.x
SDRplay API library: /usr/local/lib/libsdrplay_api.so.3
SDRplay API version: 3.15
Devices found: 2
  RSPduo, serial 1905039A32, hardware version 3
    free tuners: [A, B], modes: [SINGLE_TUNER, MASTER, DUAL_TUNER]
  RSP1A, serial 1712032B93, hardware version 255
```

## Using it

```java
try (SDRplay sdrplay = SDRplay.open();
     RspDevice device = sdrplay.select(sdrplay.devices().getFirst())) {
    device.setSampleRate(8_000_000);

    RxChannel tuner = device.channel();
    tuner.setDecimation(4, false);           // 2 MHz output
    tuner.setBandwidth(Bandwidth.KHZ_1536);
    tuner.setFrequency(162_550_000);
    tuner.setAgc(AgcMode.DISABLED);
    tuner.setGain(40, 0);                    // gain reduction dB, LNA state

    device.start(buffer -> {
        float[] iq = buffer.copyInterleavedFloats();   // I, Q, I, Q, ...
        queue.offer(iq);
    });
    Thread.sleep(10_000);
}
```

`SDRplay.open()` connects to the API service and checks the API version;
sessions share one connection, which closes with the last session.
`devices()` lists the free devices, and `select` returns a subclass of
`RspDevice` for the model, with its extra controls: `Rsp1aDevice` (RSP1A and
RSP1B), `Rsp2Device`, `RspDuoDevice`, `RspDxDevice` (RSPdx and RSPdx-R2) or
`Rsp1Device`. Device settings, such as the sample rate, are on the device;
tuner settings, such as frequency and gain, are on its `RxChannel`.

Settings can be changed before or during streaming. While streaming, each
setter sends the change to the device. The API applies gain, frequency and
sample rate changes asynchronously and rejects a second change of the same kind
while one is in progress, so jsdrplay waits up to 250 ms for the previous one to
take effect. `SampleBuffer.gainChanged()`, `frequencyChanged()` and
`sampleRateChanged()` mark the first block after each change.

Samples are 16-bit I and Q, in separate arrays as the API delivers them:
`copyI()`, `copyQ()`, `copyInterleaved()` and `copyInterleavedFloats()` copy
them. `buffer.i()` and `buffer.q()` give zero-copy access, valid only on the
callback's thread until it returns; later use throws an exception rather than
reading freed memory.

`SampleListener` and `DeviceEventListener` run on the API's threads, so hand
slow work to another thread, and do not call `stop()` or `close()` from them.
An exception thrown by a listener stops delivery and is available from
`listenerFailure()`. Power overload events are acknowledged to the API before
they reach the listener, as the API requires. On `DeviceRemoved`, close the
device from another thread.

### RSPduo

`select` picks single tuner mode on a free tuner, or slave mode when another
application is the master. Choose the mode with `selectRspDuo`:

```java
DeviceInfo info = sdrplay.devices().stream()
    .filter(d -> d.model() == RspModel.RSPDUO).findFirst().orElseThrow();
try (RspDuoDevice duo = sdrplay.selectRspDuo(info, RspDuoMode.DUAL_TUNER, Tuner.BOTH, 0)) {
    duo.channel(Tuner.A).setFrequency(155_000_000);
    duo.channel(Tuner.B).setFrequency(460_000_000);
    duo.start(buffer -> process(buffer.tuner(), buffer.copyInterleavedFloats()));
    ...
}
```

In dual tuner mode the listener is called from two threads at once, one per
tuner. Master mode takes the sample rate of both tuners, 6 or 8 MHz; another
application can then select the free tuner as slave. jsdrplay does not expose
`sdrplay_api_SwapRspDuoMode` or `sdrplay_api_SwapRspDuoActiveTuner`: close the
device and select it again in the new mode.

### Native access

Java prints a warning, which future versions will turn into an error, unless
you allow native access:

```
java --enable-native-access=ALL-UNNAMED -cp app.jar:jsdrplay.jar my.App
```

On the module path, use `--enable-native-access=io.github.dsheirer.sdrplay`. In
your own executable jar, add `Enable-Native-Access: ALL-UNNAMED` to the manifest.

### Library location

jsdrplay loads the SDRplay API library from, in order:

1. the `jsdrplay.library.path` system property: the library file, or the
   directory containing it; nothing else is tried when it is set
2. where SDRplay's installer puts it: `/usr/local/lib` on Linux and macOS,
   `%ProgramFiles%\SDRplay\API\x64` (or `ARM64`) on Windows, and a few
   distribution locations on Linux
3. the system library search path (`LD_LIBRARY_PATH`, `DYLD_LIBRARY_PATH`,
   `PATH`)

`SDRplay.isInstalled()` reports whether it can be loaded. Without it,
`SDRplay.open()` throws an `SDRplayException` with
`SDRplayError.API_NOT_FOUND` and a message listing every place tried.

### API versions

The API's parameter structures change between versions: 3.15 lengthened the
RSPduo tuner parameters, which moved the RSPdx fields after them. Using these
bindings with an older API would corrupt memory, so `SDRplay.open()` rejects
versions before 3.15 with `SDRplayError.UNSUPPORTED_API_VERSION`. Later 3.x
versions are accepted. Before releasing for one, move the pin (see
[Releases](#releases)) so that CI checks the bindings against its headers.

## Building

You need JDK 23+, plus CMake 3.16+, a C compiler and curl to run the layout
check. On Windows use Git Bash and Visual Studio.

```
./gradlew build                          # jar in build/libs
./native/probe-layouts.sh                # check the bindings against the headers
./gradlew test                           # now includes the layout check
./gradlew test -PhardwareTest=true       # also test the connected devices
```

`native/probe-layouts.sh` downloads the SDRplay installer pinned in
[`native/versions.env`](native/versions.env), checks its SHA-256, and extracts
only the headers, without running the installer. It then:

- checks that `Native.java` binds exactly the functions in `sdrplay_api.h`
  ([`check-bindings.sh`](native/check-bindings.sh))
- compiles [`layout-probe.c`](native/layout-probe.c), which prints the size and
  field offsets of every API structure, and the value of every enum constant,
  as this platform's C compiler sees them

`LayoutProbeTest` then compares each of those with `Layouts.java` and the Java
enums. CI runs this on all six platforms. Tests that need the API run when it
is installed; the hardware tests are receive-only and never turn on a bias tee.

## Releases

jsdrplay versions start with the SDRplay API version they target: jsdrplay
3.15.x is for API 3.15 and later 3.x.

| Trigger | What happens |
|---------|--------------|
| Pull request | Checks the bindings on all six platforms and builds the jar; publishes nothing |
| Merge to `master` | Replaces the `nightly` pre-release |
| Daily schedule | Checks that the pinned installer still downloads with its checksum; rebuilds the nightly if `master` changed |
| Tag `vX.Y.Z` | Publishes a release |

To release, tag the next version on the pinned API: `git tag v3.15.0 && git push
origin v3.15.0`. The workflow rejects a tag that does not start with the pinned
API version. For a pre-release add a suffix, such as `v3.15.0-rc1`.

To adopt a new API release, change `SDRPLAY_API_VERSION`,
`SDRPLAY_INSTALLER_URL` and `SDRPLAY_INSTALLER_SHA256` in
`native/versions.env`, and `SDRplay.API_VERSION`, then run
`native/probe-layouts.sh` and the tests: they list any new function, structure
change or enum value to bring into `Native.java`, `Layouts.java` and the
enums.

## License

Apache License 2.0. The SDRplay API is not part of jsdrplay and has its own
licence: see [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
