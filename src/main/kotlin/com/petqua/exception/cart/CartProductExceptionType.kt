package com.petqua.exception.cart

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class CartProductExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_CART_PRODUCT(httpStatus = NOT_FOUND, code = "CP01", errorMessage = "존재 하지 않는 봉달 상품입니다."),

    INVALID_DELIVERY_METHOD(httpStatus = BAD_REQUEST, code = "CP10", errorMessage = "유효하지 않는 배송 방법입니다."),

    PRODUCT_QUANTITY_UNDER_MINIMUM(httpStatus = BAD_REQUEST, code = "CP20", errorMessage = "최소 1개 이상의 상품을 담을 수 있습니다."),
    PRODUCT_QUANTITY_OVER_MAXIMUM(httpStatus = BAD_REQUEST, code = "CP21", errorMessage = "최대 99개까지 구매할 수 있습니다."),

    DUPLICATED_PRODUCT(httpStatus = BAD_REQUEST, code = "CP30", errorMessage = "이미 봉달 목록에 담긴 상품입니다."),
    FORBIDDEN_CART_PRODUCT(httpStatus = FORBIDDEN, code = "CP31", errorMessage = "해당 봉달 상품에 대한 권한이 없습니다."),
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
