package com.petqua.exception.product

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ProductExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_PRODUCT(NOT_FOUND, "P01", "존재하지 않는 상품입니다."),

    INVALID_SEARCH_WORD(BAD_REQUEST, "P10", "유효하지 않은 검색어입니다."),

    WISH_COUNT_UNDER_MINIMUM(BAD_REQUEST, "P20", "찜 개수는 0 이상이어야 합니다."),

    INVALID_PRODUCT_OPTION(BAD_REQUEST, "P30", "유효하지 않은 상품 옵션입니다."),

    INVALID_DELIVERY_METHOD(BAD_REQUEST, "P31", "유효하지 않는 배송 방법입니다."),
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
