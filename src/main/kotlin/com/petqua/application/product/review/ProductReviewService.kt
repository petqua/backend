package com.petqua.application.product.review

import com.petqua.application.product.dto.MemberProductReviewReadQuery
import com.petqua.application.product.dto.MemberProductReviewResponse
import com.petqua.application.product.dto.MemberProductReviewsResponse
import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.ProductReviewResponse
import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.dto.UpdateReviewRecommendationCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewImage
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

    fun create(productReview: ProductReview, reviewImageUrls: List<String>): Long {
        val savedProductReview = productReviewRepository.save(productReview)
        val images = reviewImageUrls.map {
            ProductReviewImage(imageUrl = it, productReviewId = savedProductReview.id)
        }
        productReviewImageRepository.saveAll(images)
        return savedProductReview.id
    }

    @Transactional(readOnly = true)
    fun readAll(query: ProductReviewReadQuery): ProductReviewsResponse {
        val reviewsByCondition = productReviewRepository.findAllByCondition(
            query.toCondition(),
            query.toPaging(),
        )
        val reviewIds = reviewsByCondition.map { it.id }
        val imagesByReview = getImagesByReviewIds(reviewIds)
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

    private fun getImagesByReviewIds(productReviewIds: List<Long>): Map<Long, List<String>> {
        return productReviewImageRepository.findAllByProductReviewIdIn(productReviewIds).groupBy { it.productReviewId }
            .mapValues { it.value.map { image -> image.imageUrl } }
    }

    fun readMemberProductReviews(query: MemberProductReviewReadQuery): MemberProductReviewsResponse {
        val memberProductReviews = productReviewRepository.findMemberProductReviewBy(query.memberId, query.toPaging())
        val reviewIds = memberProductReviews.map { it.reviewId }
        val imagesByReview = getImagesByReviewIds(reviewIds)

        val responses = memberProductReviews.map {
            MemberProductReviewResponse(it, imagesByReview[it.reviewId] ?: emptyList())
        }
        return MemberProductReviewsResponse.of(responses, query.limit)
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
        )?.let { delete(it) } ?: saveReviewRecommendation(command.toReviewRecommendation())
    }

    private fun saveReviewRecommendation(productReviewRecommendation: ProductReviewRecommendation) {
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
