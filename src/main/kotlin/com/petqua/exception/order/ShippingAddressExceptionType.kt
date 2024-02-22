package com.petqua.exception.order

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class ShippingAddressExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_SHIPPING_ADDRESS(BAD_REQUEST, "SA01", "존재하지 않는 배송지입니다."),
    INVALID_PHONE_NUMBER(BAD_REQUEST, "SA02", "잘못된 휴대전화 번호입니다."),
    EMPTY_ADDRESS(BAD_REQUEST, "SA03", "주소가 입력되지 않았습니다."),
    EMPTY_DETAIL_ADDRESS(BAD_REQUEST, "SA04", "상세주소가 입력되지 않았습니다."),
    EMPTY_NAME(BAD_REQUEST, "SA05", "배송지 이름이 입력되지 않았습니다."),
    EMPTY_RECEIVER(BAD_REQUEST, "SA06", "받는 사람이 입력되지 않았습니다."),
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
