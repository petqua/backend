package com.petqua.domain.product.review

import org.springframework.data.jpa.repository.JpaRepository

interface ProductReviewRecommendationRepository : JpaRepository<ProductReviewRecommendation, Long> {

    fun findByProductReviewIdAndMemberId(productReviewId: Long, memberId: Long): ProductReviewRecommendation?

    fun findAllByMemberIdAndProductReviewIdIn(
        memberId: Long,
        productReviewIds: List<Long>
    ): Set<ProductReviewRecommendation>
}
