// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;
import jdk.incubator.foreign.*;

public class sdrplay_api_PowerOverloadCbParamT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_INT$LAYOUT.withName("powerOverloadChangeType")
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_PowerOverloadCbParamT.$struct$LAYOUT;
    }
    static final VarHandle powerOverloadChangeType$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("powerOverloadChangeType"));
    public static VarHandle powerOverloadChangeType$VH() {
        return sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$VH;
    }
    public static int powerOverloadChangeType$get(MemorySegment seg) {
        return (int)sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$VH.get(seg);
    }
    public static void powerOverloadChangeType$set( MemorySegment seg, int x) {
        sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$VH.set(seg, x);
    }
    public static int powerOverloadChangeType$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void powerOverloadChangeType$set(MemorySegment seg, long index, int x) {
        sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$VH.set(seg.asSlice(index*sizeof()), x);
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


