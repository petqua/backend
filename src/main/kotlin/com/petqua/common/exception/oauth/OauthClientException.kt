package com.petqua.common.exception.oauth

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType
import com.petqua.common.exception.oauth.OauthClientExceptionType.UNSUPPORTED_OAUTH_SERVER_TYPE

class OauthClientException : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return UNSUPPORTED_OAUTH_SERVER_TYPE
    }
}
