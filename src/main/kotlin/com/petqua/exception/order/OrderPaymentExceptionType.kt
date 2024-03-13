package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus

enum class OrderPaymentExceptionType (
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    ORDER_PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "OP01", "주문 결제 내역이 존재하지 않습니다."),

    FAIL_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, "OP02", "주문 결제 내역 저장에 실패하였습니다."),
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
