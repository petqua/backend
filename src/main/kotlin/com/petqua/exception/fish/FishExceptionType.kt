package com.petqua.exception.fish

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST

enum class FishExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    INVALID_SPECIES_NAME(BAD_REQUEST, "FH01", "유효하지 않은 어종 이름입니다."),
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
