package com.petqua.common.exception

abstract class BaseException : RuntimeException() {

    abstract fun exceptionType(): BaseExceptionType
}
