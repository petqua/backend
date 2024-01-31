package com.petqua.common.util

inline fun throwExceptionWhen(condition: Boolean, exceptionSupplier: () -> RuntimeException) {
    if (condition) throw exceptionSupplier()
}
