package com.petqua.common.util

inline fun <reified K, V> Map<K, V>.getOrThrow(
    key: K,
    exceptionSupplier: () -> Exception = {
        IllegalArgumentException("Not exist key: $key")
    }
): V {
    return this[key] ?: throw exceptionSupplier()
}
