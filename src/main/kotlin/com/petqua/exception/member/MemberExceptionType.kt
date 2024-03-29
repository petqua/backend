package com.petqua.exception.member

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND

enum class MemberExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_MEMBER(NOT_FOUND, "M01", "존재하지 않는 회원입니다."),

    INVALID_MEMBER_STATE(INTERNAL_SERVER_ERROR, "M90", "유효하지 않은 회원 상태입니다.")
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
