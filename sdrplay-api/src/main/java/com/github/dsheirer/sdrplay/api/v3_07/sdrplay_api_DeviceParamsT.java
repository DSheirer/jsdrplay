// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;
import jdk.incubator.foreign.*;

public class sdrplay_api_DeviceParamsT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_POINTER$LAYOUT.withName("devParams"),
        Constants$root.C_POINTER$LAYOUT.withName("rxChannelA"),
        Constants$root.C_POINTER$LAYOUT.withName("rxChannelB")
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_DeviceParamsT.$struct$LAYOUT;
    }
    static final VarHandle devParams$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("devParams"));
    public static VarHandle devParams$VH() {
        return sdrplay_api_DeviceParamsT.devParams$VH;
    }
    public static MemoryAddress devParams$get(MemorySegment seg) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.devParams$VH.get(seg);
    }
    public static void devParams$set( MemorySegment seg, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.devParams$VH.set(seg, x);
    }
    public static MemoryAddress devParams$get(MemorySegment seg, long index) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.devParams$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void devParams$set(MemorySegment seg, long index, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.devParams$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle rxChannelA$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rxChannelA"));
    public static VarHandle rxChannelA$VH() {
        return sdrplay_api_DeviceParamsT.rxChannelA$VH;
    }
    public static MemoryAddress rxChannelA$get(MemorySegment seg) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.rxChannelA$VH.get(seg);
    }
    public static void rxChannelA$set( MemorySegment seg, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.rxChannelA$VH.set(seg, x);
    }
    public static MemoryAddress rxChannelA$get(MemorySegment seg, long index) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.rxChannelA$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void rxChannelA$set(MemorySegment seg, long index, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.rxChannelA$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle rxChannelB$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rxChannelB"));
    public static VarHandle rxChannelB$VH() {
        return sdrplay_api_DeviceParamsT.rxChannelB$VH;
    }
    public static MemoryAddress rxChannelB$get(MemorySegment seg) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.rxChannelB$VH.get(seg);
    }
    public static void rxChannelB$set( MemorySegment seg, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.rxChannelB$VH.set(seg, x);
    }
    public static MemoryAddress rxChannelB$get(MemorySegment seg, long index) {
        return (jdk.incubator.foreign.MemoryAddress)sdrplay_api_DeviceParamsT.rxChannelB$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void rxChannelB$set(MemorySegment seg, long index, MemoryAddress x) {
        sdrplay_api_DeviceParamsT.rxChannelB$VH.set(seg.asSlice(index*sizeof()), x);
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


