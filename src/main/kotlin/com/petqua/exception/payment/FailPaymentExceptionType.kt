package com.petqua.exception.payment

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class FailPaymentExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_CODE(BAD_REQUEST, "PF01", "지원하지 않는 결제 실패 코드입니다."),
    ORDER_NUMBER_MISSING_EXCEPTION(BAD_REQUEST, "PF02", "주문번호가 입력되지 않았습니다."),
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
