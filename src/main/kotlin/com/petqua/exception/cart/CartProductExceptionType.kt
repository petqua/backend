package com.petqua.exception.cart

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class CartProductExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_DELIVERY_METHOD(httpStatus = BAD_REQUEST, errorMessage = "유효하지 않는 배송 방법입니다."),
    PRODUCT_QUANTITY_UNDER_MINIMUM(httpStatus = BAD_REQUEST, errorMessage = "최소 1개 이상의 상품을 담을 수 있습니다."),
    PRODUCT_QUANTITY_OVER_MAXIMUM(httpStatus = BAD_REQUEST, errorMessage = "최대 99개까지 구매할 수 있습니다."),
    DUPLICATED_PRODUCT(httpStatus = BAD_REQUEST, errorMessage = "이미 장바구니에 담긴 상품입니다."),
    NOT_FOUND_CART_PRODUCT(httpStatus = NOT_FOUND, errorMessage = "존재 하지 않는 봉달 상품입니다."),
    FORBIDDEN_CART_PRODUCT(httpStatus = FORBIDDEN, errorMessage = "해당 봉달 상품에 대한 권한이 없습니다."),
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
