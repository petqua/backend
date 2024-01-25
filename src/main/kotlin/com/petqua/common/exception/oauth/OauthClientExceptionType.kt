package com.petqua.common.exception.oauth

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class OauthClientExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    UNSUPPORTED_OAUTH_SERVER_TYPE(BAD_REQUEST, "지원하지 않는 소셜 로그인 타입입니다.")
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
