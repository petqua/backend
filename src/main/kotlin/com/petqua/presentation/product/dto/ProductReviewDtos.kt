package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.domain.product.dto.REVIEW_LIMIT_CEILING
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC

data class ReadAllProductReviewsRequest(
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val lastViewedId: Long,
    val photoOnly: Boolean = false,
    val score: Int? = null,
    val limit: Int = REVIEW_LIMIT_CEILING,
) {
    fun toCommand(productId: Long, memberId: Long?): ProductReviewReadQuery {
        return ProductReviewReadQuery(
            productId = productId,
            memberId = memberId,
            sorter = sorter,
            score = score,
            photoOnly = photoOnly,
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}
