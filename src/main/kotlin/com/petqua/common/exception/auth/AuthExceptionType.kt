package com.petqua.common.exception.auth

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class AuthExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_TOKEN(BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(BAD_REQUEST, "만료된 토큰입니다."),
    INVALID_ACCESS_TOKEN(BAD_REQUEST, "올바른 형태의 AccessToken이 아닙니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "올바른 형태의 RefreshToken이 아닙니다."),

    UNSUPPORTED_AUTHORITY(BAD_REQUEST, "해당하는 권한이 존재하지 않습니다."),

    INVALID_REQUEST(BAD_REQUEST, "올바르지 않은 요청입니다."),
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}