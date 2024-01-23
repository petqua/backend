package com.petqua.common.exception

import org.springframework.http.HttpStatus

interface BaseExceptionType {

    fun httpStatus(): HttpStatus

    fun errorMessage(): String
}
