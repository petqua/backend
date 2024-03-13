package com.petqua.exception.product

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus

enum class ProductSnapshotExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_PRODUCT_SNAPSHOT(HttpStatus.NOT_FOUND, "PS01", "존재하지 않는 상품 스냅샷입니다."),
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
