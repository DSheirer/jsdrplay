// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import jdk.incubator.foreign.*;

public interface sdrplay_api_GetErrorString_t {

    jdk.incubator.foreign.Addressable apply(int x0);
    static NativeSymbol allocate(sdrplay_api_GetErrorString_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(sdrplay_api_GetErrorString_t.class, fi, constants$3.sdrplay_api_GetErrorString_t$FUNC, "(I)Ljdk/incubator/foreign/Addressable;", scope);
    }
    static sdrplay_api_GetErrorString_t ofAddress(MemoryAddress addr, ResourceScope scope) {
        NativeSymbol symbol = NativeSymbol.ofAddress("sdrplay_api_GetErrorString_t::" + Long.toHexString(addr.toRawLongValue()), addr, scope);
return (int x0) -> {
            try {
                return (jdk.incubator.foreign.Addressable)(jdk.incubator.foreign.MemoryAddress)constants$3.sdrplay_api_GetErrorString_t$MH.invokeExact(symbol, x0);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}


