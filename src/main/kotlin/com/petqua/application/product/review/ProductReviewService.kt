package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.ProductReviewResponse
import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.dto.UpdateReviewRecommendationCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRecommendation
import com.petqua.domain.product.review.ProductReviewRecommendationRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.product.review.ProductReviewStatistics
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.NOT_FOUND_PRODUCT_REVIEW
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ProductReviewService(
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
    private val productReviewRecommendationRepository: ProductReviewRecommendationRepository,
) {

    @Transactional(readOnly = true)
    fun readAll(query: ProductReviewReadQuery): ProductReviewsResponse {
        val reviewsByCondition = productReviewRepository.findAllByCondition(
            query.toCondition(),
            query.toPaging(),
        )
        val imagesByReview = getImagesByReview(reviewsByCondition)
        val responses = reviewsByCondition.map {
            ProductReviewResponse(it, imagesByReview[it.id] ?: emptyList())
        }

        if (query.loginMemberOrGuest.isMember()) {
            val recommendations = productReviewRecommendationRepository.findAllByMemberIdAndProductReviewIdIn(
                query.loginMemberOrGuest.memberId,
                responses.map { it.id },
            ).map { it.productReviewId }

            val recommendationMarkedResponses = responses.map { it.copy(recommended = recommendations.contains(it.id)) }
            return ProductReviewsResponse.of(recommendationMarkedResponses, query.limit)
        }
        return ProductReviewsResponse.of(responses, query.limit)
    }

    private fun getImagesByReview(reviewsByCondition: List<ProductReviewWithMemberResponse>): Map<Long, List<String>> {
        val productReviewIds = reviewsByCondition.map { it.id }
        return productReviewImageRepository.findAllByProductReviewIdIn(productReviewIds).groupBy { it.productReviewId }
            .mapValues { it.value.map { image -> image.imageUrl } }
    }

    @Transactional(readOnly = true)
    fun readReviewCountStatistics(productId: Long): ProductReviewStatisticsResponse {
        val reviewScoreWithCounts = productReviewRepository.findReviewScoresWithCount(productId)
        val productReviewStatistics = ProductReviewStatistics.from(reviewScoreWithCounts)
        return ProductReviewStatisticsResponse.from(productReviewStatistics)
    }

    fun updateReviewRecommendation(command: UpdateReviewRecommendationCommand) {
        productReviewRecommendationRepository.findByProductReviewIdAndMemberId(
            command.productReviewId,
            command.memberId,
        )?.let { delete(it) } ?: save(command.toReviewRecommendation())
    }

    private fun save(productReviewRecommendation: ProductReviewRecommendation) {
        productReviewRecommendationRepository.save(productReviewRecommendation)
        val productReview = productReviewRepository.findByIdOrThrow(productReviewRecommendation.productReviewId) {
            ProductReviewException(NOT_FOUND_PRODUCT_REVIEW)
        }
        productReview.increaseRecommendCount()
    }

    private fun delete(productReviewRecommendation: ProductReviewRecommendation) {
        productReviewRecommendationRepository.delete(productReviewRecommendation)
        val productReview = productReviewRepository.findByIdOrThrow(productReviewRecommendation.productReviewId) {
            ProductReviewException(NOT_FOUND_PRODUCT_REVIEW)
        }
        productReview.decreaseRecommendCount()
    }
}
