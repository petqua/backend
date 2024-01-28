package com.petqua.common.exception.auth

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class OauthClientException(
    private val oauthClientExceptionType: OauthClientExceptionType
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return oauthClientExceptionType
    }
}