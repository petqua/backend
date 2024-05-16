package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class OrderExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    PRODUCT_NOT_FOUND(BAD_REQUEST, "O01", "주문한 상품이 존재하지 않습니다."),

    ORDER_TOTAL_PRICE_NOT_MATCH(BAD_REQUEST, "O10", "주문한 상품의 총 가격이 일치하지 않습니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "O11", "존재하지 않는 주문입니다."),
    PAYMENT_PRICE_NOT_MATCH(BAD_REQUEST, "O12", "주문한 상품 가격과 결제 금액이 일치하지 않습니다."),
    EMPTY_SHIPPING_ADDRESS(BAD_REQUEST, "O13", "배송지가 입력되지 않았습니다."),
    STORE_NOT_FOUND(BAD_REQUEST, "O14", "주문한 상품의 상점이 존재하지 않았습니다."),
    PRODUCT_INFO_NOT_MATCH(BAD_REQUEST, "O15", "주문한 상품이 등록된 상품의 정보와 일치하지 않습니다."),

    INVALID_PAYMENT_TYPE(BAD_REQUEST, "O20", "유효하지 않은 결제 방식입니다."),

    FORBIDDEN_ORDER(FORBIDDEN, "O30", "해당 주문에 대한 권한이 없습니다."),
    ORDER_CAN_NOT_CANCEL(BAD_REQUEST, "O31", "취소할 수 없는 주문입니다."),
    ORDER_CAN_NOT_PAY(BAD_REQUEST, "O32", "결제할 수 없는 주문입니다."),

    NOT_INVALID_ORDER_READ_QUERY(BAD_REQUEST, "O40", "유효하지 않은 주문 조회 조건입니다."),
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
