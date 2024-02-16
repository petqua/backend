package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus

enum class ShippingAddressExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_PHONE_NUMBER(HttpStatus.NOT_FOUND, "SA02", "잘못된 휴대전화 번호입니다."),
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
