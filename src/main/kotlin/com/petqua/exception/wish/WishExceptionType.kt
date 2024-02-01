package com.petqua.exception.wish

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND

enum class WishExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_WISH(httpStatus = NOT_FOUND, "존재하지 않는 찜 상품입니다."),
    FORBIDDEN_WISH(httpStatus = FORBIDDEN, "해당 찜 상품에 대한 권한이 없습니다."),
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}