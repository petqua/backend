package com.petqua.application.product.review

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.ProductReviewResponse
import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse
import com.petqua.domain.product.review.ProductReviewImageRepository
import com.petqua.domain.product.review.ProductReviewRepository
import com.petqua.domain.product.review.ProductReviewStatistics
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ProductReviewService(
    private val productReviewRepository: ProductReviewRepository,
    private val productReviewImageRepository: ProductReviewImageRepository,
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
        // TODO: 추천 여부 반영
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
}
