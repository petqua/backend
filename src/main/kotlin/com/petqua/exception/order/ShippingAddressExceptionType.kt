package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus

enum class ShippingAddressExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_SHIPPING_ADDRESS(HttpStatus.NOT_FOUND, "SA01", "배송지가 존재하지 않습니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "SA02", "잘못된 휴대전화 번호입니다."),
    EMPTY_ADDRESS(HttpStatus.BAD_REQUEST, "SA03", "주소가 입력되지 않았습니다."),
    EMPTY_DETAIL_ADDRESS(HttpStatus.BAD_REQUEST, "SA04", "상세주소가 입력되지 않았습니다."),
    EMPTY_NAME(HttpStatus.BAD_REQUEST, "SA05", "배송지 이름이 입력되지 않았습니다."),
    EMPTY_RECEIVER(HttpStatus.BAD_REQUEST, "SA06", "받는 사람이 입력되지 않았습니다."),
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
