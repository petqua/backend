package com.petqua.exception.product.review

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND

enum class ProductReviewExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_PRODUCT_REVIEW(NOT_FOUND, "PR01", "존재하지 않는 리뷰입니다."),
    REVIEW_SCORE_OUT_OF_RANGE(BAD_REQUEST, "PR02", "리뷰 별점은 1점부터 5점까지만 가능합니다."),
    REVIEW_CONTENT_LENGTH_OUT_OF_RANGE(BAD_REQUEST, "PR03", "리뷰는 최소 10자 최대 300자 작성할 수 있습니다."),
    EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT(BAD_REQUEST, "PR04", "최대 리뷰 사진 업로드 개수를 초과했습니다"),
    FAILED_REVIEW_IMAGE_UPLOAD(INTERNAL_SERVER_ERROR, "PR05", "리뷰 사진 업로드에 실패했습니다."),
    UNSUPPORTED_IMAGE_TYPE(BAD_REQUEST, "PR06", "지원하지 않는 리뷰 이미지 형식입니다."),
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
