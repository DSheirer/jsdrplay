// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import jdk.incubator.foreign.*;

public interface sdrplay_api_ApiVersion_t {

    int apply(jdk.incubator.foreign.MemoryAddress x0);
    static NativeSymbol allocate(sdrplay_api_ApiVersion_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(sdrplay_api_ApiVersion_t.class, fi, constants$1.sdrplay_api_ApiVersion_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;)I", scope);
    }
    static sdrplay_api_ApiVersion_t ofAddress(MemoryAddress addr, ResourceScope scope) {
        NativeSymbol symbol = NativeSymbol.ofAddress("sdrplay_api_ApiVersion_t::" + Long.toHexString(addr.toRawLongValue()), addr, scope);
return (jdk.incubator.foreign.MemoryAddress x0) -> {
            try {
                return (int)constants$1.sdrplay_api_ApiVersion_t$MH.invokeExact(symbol, (jdk.incubator.foreign.Addressable)x0);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}


