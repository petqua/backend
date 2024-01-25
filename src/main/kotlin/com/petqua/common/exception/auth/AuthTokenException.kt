package com.petqua.common.exception.auth

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class AuthTokenException(
    private val authTokenExceptionType: AuthTokenExceptionType
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return authTokenExceptionType
    }
}
