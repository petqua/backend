package com.petqua.exception.product.review

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ProductReviewExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_PRODUCT_REVIEW(NOT_FOUND, "PR01", "존재하지 않는 리뷰입니다."),
    REVIEW_SCORE_OUT_OF_RANGE(NOT_FOUND, "PR02", "리뷰 별점은 1점부터 5점까지만 가능합니다.")
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
