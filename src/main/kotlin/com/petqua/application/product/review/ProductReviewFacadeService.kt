package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewCreateCommand
import org.springframework.stereotype.Service

@Service
class ProductReviewFacadeService(
    private val productReviewService: ProductReviewService,
    private val productReviewImageUploader: ProductReviewImageUploader,
) {

    fun create(command: ProductReviewCreateCommand): Long {
        val productReview = command.toProductReview()
        val reviewImageUrls = productReviewImageUploader.uploadAll(command.images)
        return productReviewService.create(productReview, reviewImageUrls)
    }
}
