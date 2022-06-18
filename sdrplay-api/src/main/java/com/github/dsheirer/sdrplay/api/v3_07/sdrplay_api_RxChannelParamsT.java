// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import jdk.incubator.foreign.*;

public class sdrplay_api_RxChannelParamsT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("bwType"),
            Constants$root.C_INT$LAYOUT.withName("ifType"),
            Constants$root.C_INT$LAYOUT.withName("loMode"),
            MemoryLayout.structLayout(
                Constants$root.C_INT$LAYOUT.withName("gRdB"),
                Constants$root.C_CHAR$LAYOUT.withName("LNAstate"),
                Constants$root.C_CHAR$LAYOUT.withName("syncUpdate"),
                MemoryLayout.paddingLayout(16),
                Constants$root.C_INT$LAYOUT.withName("minGr"),
                MemoryLayout.structLayout(
                    Constants$root.C_FLOAT$LAYOUT.withName("curr"),
                    Constants$root.C_FLOAT$LAYOUT.withName("max"),
                    Constants$root.C_FLOAT$LAYOUT.withName("min")
                ).withName("gainVals")
            ).withName("gain"),
            MemoryLayout.paddingLayout(32),
            MemoryLayout.structLayout(
                Constants$root.C_DOUBLE$LAYOUT.withName("rfHz"),
                Constants$root.C_CHAR$LAYOUT.withName("syncUpdate"),
                MemoryLayout.paddingLayout(56)
            ).withName("rfFreq"),
            MemoryLayout.structLayout(
                Constants$root.C_CHAR$LAYOUT.withName("dcCal"),
                Constants$root.C_CHAR$LAYOUT.withName("speedUp"),
                MemoryLayout.paddingLayout(16),
                Constants$root.C_INT$LAYOUT.withName("trackTime"),
                Constants$root.C_INT$LAYOUT.withName("refreshRateTime")
            ).withName("dcOffsetTuner"),
            MemoryLayout.paddingLayout(32)
        ).withName("tunerParams"),
        MemoryLayout.structLayout(
            MemoryLayout.structLayout(
                Constants$root.C_CHAR$LAYOUT.withName("DCenable"),
                Constants$root.C_CHAR$LAYOUT.withName("IQenable")
            ).withName("dcOffset"),
            MemoryLayout.structLayout(
                Constants$root.C_CHAR$LAYOUT.withName("enable"),
                Constants$root.C_CHAR$LAYOUT.withName("decimationFactor"),
                Constants$root.C_CHAR$LAYOUT.withName("wideBandSignal")
            ).withName("decimation"),
            MemoryLayout.paddingLayout(24),
            MemoryLayout.structLayout(
                Constants$root.C_INT$LAYOUT.withName("enable"),
                Constants$root.C_INT$LAYOUT.withName("setPoint_dBfs"),
                Constants$root.C_SHORT$LAYOUT.withName("attack_ms"),
                Constants$root.C_SHORT$LAYOUT.withName("decay_ms"),
                Constants$root.C_SHORT$LAYOUT.withName("decay_delay_ms"),
                Constants$root.C_SHORT$LAYOUT.withName("decay_threshold_dB"),
                Constants$root.C_INT$LAYOUT.withName("syncUpdate")
            ).withName("agc"),
            Constants$root.C_INT$LAYOUT.withName("adsbMode")
        ).withName("ctrlParams"),
        MemoryLayout.structLayout(
            Constants$root.C_CHAR$LAYOUT.withName("biasTEnable")
        ).withName("rsp1aTunerParams"),
        MemoryLayout.paddingLayout(24),
        MemoryLayout.structLayout(
            Constants$root.C_CHAR$LAYOUT.withName("biasTEnable"),
            MemoryLayout.paddingLayout(24),
            Constants$root.C_INT$LAYOUT.withName("amPortSel"),
            Constants$root.C_INT$LAYOUT.withName("antennaSel"),
            Constants$root.C_CHAR$LAYOUT.withName("rfNotchEnable"),
            MemoryLayout.paddingLayout(24)
        ).withName("rsp2TunerParams"),
        MemoryLayout.structLayout(
            Constants$root.C_CHAR$LAYOUT.withName("biasTEnable"),
            MemoryLayout.paddingLayout(24),
            Constants$root.C_INT$LAYOUT.withName("tuner1AmPortSel"),
            Constants$root.C_CHAR$LAYOUT.withName("tuner1AmNotchEnable"),
            Constants$root.C_CHAR$LAYOUT.withName("rfNotchEnable"),
            Constants$root.C_CHAR$LAYOUT.withName("rfDabNotchEnable"),
            MemoryLayout.paddingLayout(8)
        ).withName("rspDuoTunerParams"),
        MemoryLayout.structLayout(
            Constants$root.C_INT$LAYOUT.withName("hdrBw")
        ).withName("rspDxTunerParams"),
        MemoryLayout.paddingLayout(32)
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_RxChannelParamsT.$struct$LAYOUT;
    }
    public static MemorySegment tunerParams$slice(MemorySegment seg) {
        return seg.asSlice(0, 72);
    }
    public static MemorySegment ctrlParams$slice(MemorySegment seg) {
        return seg.asSlice(72, 32);
    }
    public static MemorySegment rsp1aTunerParams$slice(MemorySegment seg) {
        return seg.asSlice(104, 1);
    }
    public static MemorySegment rsp2TunerParams$slice(MemorySegment seg) {
        return seg.asSlice(108, 16);
    }
    public static MemorySegment rspDuoTunerParams$slice(MemorySegment seg) {
        return seg.asSlice(124, 12);
    }
    public static MemorySegment rspDxTunerParams$slice(MemorySegment seg) {
        return seg.asSlice(136, 4);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(int len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment allocate(ResourceScope scope) { return allocate(SegmentAllocator.nativeAllocator(scope)); }
    public static MemorySegment allocateArray(int len, ResourceScope scope) {
        return allocateArray(len, SegmentAllocator.nativeAllocator(scope));
    }
    public static MemorySegment ofAddress(MemoryAddress addr, ResourceScope scope) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, scope); }
}

