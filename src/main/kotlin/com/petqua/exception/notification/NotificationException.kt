package com.petqua.exception.notification

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class NotificationException(
    private val exceptionType: NotificationExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
