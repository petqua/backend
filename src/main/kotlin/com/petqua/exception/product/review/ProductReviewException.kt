package com.petqua.exception.product.review

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class ProductReviewException(
    private val exceptionType: ProductReviewExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
