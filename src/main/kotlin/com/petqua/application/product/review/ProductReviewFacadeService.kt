package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewCreateCommand
import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.dto.UpdateReviewRecommendationCommand
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

    fun readAll(query: ProductReviewReadQuery): ProductReviewsResponse {
        return productReviewService.readAll(query)
    }

    fun readReviewCountStatistics(productId: Long): ProductReviewStatisticsResponse {
        return productReviewService.readReviewCountStatistics(productId)
    }

    fun updateReviewRecommendation(command: UpdateReviewRecommendationCommand) {
        productReviewService.updateReviewRecommendation(command)
    }
}
