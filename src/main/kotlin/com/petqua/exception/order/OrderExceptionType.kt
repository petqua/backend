package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class OrderExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    PRODUCT_NOT_FOUND(BAD_REQUEST, "O01", "주문한 상품이 존재하지 않습니다."),

    ORDER_PRICE_NOT_MATCH(BAD_REQUEST, "O10", "주문한 상품의 가격이 일치하지 않습니다."),
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
