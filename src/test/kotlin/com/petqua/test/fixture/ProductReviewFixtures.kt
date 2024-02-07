package com.petqua.test.fixture

import com.petqua.domain.product.review.ProductReview

fun productReview(
    id: Long = 0L,
    content: String = "content",
    productId: Long,
    reviewerId: Long,
    score: Int = 5,
    recommendCount: Int = 0,
    hasPhotos: Boolean = false,
): ProductReview {
    return ProductReview(
        id = id,
        content = content,
        productId = productId,
        memberId = reviewerId,
        score = score,
        recommendCount = recommendCount,
        hasPhotos = hasPhotos,
    )
}
