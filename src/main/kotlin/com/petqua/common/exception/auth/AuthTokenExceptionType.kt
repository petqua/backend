package com.petqua.common.exception.auth

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class AuthTokenExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_TOKEN(BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(BAD_REQUEST, "만료된 토큰입니다.")
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
