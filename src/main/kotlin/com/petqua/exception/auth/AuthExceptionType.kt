package com.petqua.exception.auth

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.UNAUTHORIZED

enum class AuthExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    EXPIRED_ACCESS_TOKEN(BAD_REQUEST, "A01", "만료된 AccessToken입니다."),
    EXPIRED_REFRESH_TOKEN(BAD_REQUEST, "A02", "만료된 RefreshToken입니다."),
    EXPIRED_SIGN_UP_TOKEN(BAD_REQUEST, "A03", "만료된 SignUpToken입니다."),

    INVALID_ACCESS_TOKEN(BAD_REQUEST, "A10", "올바른 형태의 AccessToken이 아닙니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "A11", "올바른 형태의 RefreshToken이 아닙니다."),
    INVALID_AUTH_HEADER(BAD_REQUEST, "A12", "올바른 형태의 Authorization 헤더가 아닙니다."),
    INVALID_AUTH_COOKIE(BAD_REQUEST, "A13", "올바른 형태의 쿠키가 아닙니다."),
    UNABLE_ACCESS_TOKEN(UNAUTHORIZED, "A14", "사용할 수 없는 AccessToken입니다."),
    INVALID_SIGN_UP_TOKEN(BAD_REQUEST, "A15", "올바른 형태의 SignUpToken이 아닙니다."),
    INVALID_SIGN_UP_AUTH_HEADER(BAD_REQUEST, "A16", "올바른 형태의 Sign Up Authorization 헤더가 아닙니다."),

    UNSUPPORTED_AUTHORITY(BAD_REQUEST, "A20", "해당하는 권한이 존재하지 않습니다."),

    NOT_RENEWABLE_ACCESS_TOKEN(BAD_REQUEST, "A30", "유효한 AccessToken은 갱신할 수 없습니다."),

    UNSUPPORTED_OPERATION(HttpStatus.INTERNAL_SERVER_ERROR, "A40", "지원하지 않는 메서드를 호출했습니다."),
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
