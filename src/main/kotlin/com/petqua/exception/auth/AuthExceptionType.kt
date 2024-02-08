package com.petqua.exception.auth

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class AuthExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    EXPIRED_ACCESS_TOKEN(BAD_REQUEST, "A01", "만료된 AccessToken입니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "A02", "만료된 RefreshToken입니다."),

    NOT_RENEWABLE_ACCESS_TOKEN(BAD_REQUEST, "A10", "유효한 AccessToken은 갱신할 수 없습니다."),
    INVALID_ACCESS_TOKEN(BAD_REQUEST, "A11", "올바른 형태의 AccessToken이 아닙니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "A13", "올바른 형태의 RefreshToken이 아닙니다."),
    INVALID_AUTH_HEADER(BAD_REQUEST, "A14", "올바른 형태의 Authorization 헤더가 아닙니다."),
    INVALID_AUTH_COOKIE(BAD_REQUEST, "A15", "올바른 형태의 쿠키가 아닙니다."),

    UNSUPPORTED_AUTHORITY(BAD_REQUEST, "A20", "해당하는 권한이 존재하지 않습니다."),
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
