package com.petqua.exception.notification

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN

enum class NotificationExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOTIFICATION_NOT_FOUND(BAD_REQUEST, "N01", "존재하지 않는 알림입니다."),

    FORBIDDEN_NOTIFICATION(FORBIDDEN, "N10", "알림에 대한 권한이 없습니다.")
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun code(): String {
        return code
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
