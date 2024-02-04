package com.petqua.exception.product

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ProductExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다."),
    INVALID_SEARCH_WORD(BAD_REQUEST, "유효하지 않은 검색어입니다.")
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
