package com.petqua.test.fixture

import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewImage
import com.petqua.domain.product.review.ProductReviewScore

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
        score = ProductReviewScore(score),
        recommendCount = recommendCount,
        hasPhotos = hasPhotos,
    )
}

fun productReviewImage(
    id: Long = 0L,
    imageUrl: String = "imageUrl",
    productReviewId: Long,
): ProductReviewImage {
    return ProductReviewImage(
        id = id,
        imageUrl = imageUrl,
        productReviewId = productReviewId,
    )
}
