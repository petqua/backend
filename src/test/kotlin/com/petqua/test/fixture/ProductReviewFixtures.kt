package com.petqua.test.fixture

import com.petqua.application.product.dto.ProductReviewCreateCommand
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewContent
import com.petqua.domain.product.review.ProductReviewImage
import com.petqua.domain.product.review.ProductReviewScore
import org.springframework.web.multipart.MultipartFile

fun productReview(
    id: Long = 0L,
    content: String = "This is a product review content",
    productId: Long,
    reviewerId: Long,
    score: Int = 5,
    recommendCount: Int = 0,
    hasPhotos: Boolean = false,
): ProductReview {
    return ProductReview(
        id = id,
        content = ProductReviewContent(content),
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

fun productReviewCreateCommand(
    productId: Long = 0L,
    memberId: Long = 0L,
    score: Int,
    content: String,
    images: List<MultipartFile>,
): ProductReviewCreateCommand {
    return ProductReviewCreateCommand(
        productId = productId,
        memberId = memberId,
        score = score,
        content = content,
        images = images,
    )
}
