package com.petqua.exception.store

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND

enum class StoreExceptionType(
    private val httpStatus: HttpStatus,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_STORE(NOT_FOUND, "존재하지 않는 상점입니다."),
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}
