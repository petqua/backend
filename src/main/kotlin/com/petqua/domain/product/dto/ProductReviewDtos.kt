package com.petqua.domain.product.dto

import com.petqua.domain.member.Member
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import java.time.LocalDateTime

data class ProductReviewReadCondition(
    val productId: Long,
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val score: Int? = null,
    val photoOnly: Boolean,
)

data class ProductReviewWithMemberResponse(
    val id: Long,
    val productId: Long,
    val score: Int,
    val content: String,
    val createdAt: LocalDateTime,
    val hasPhotos: Boolean,
    val recommendCount: Int,
    val reviewerId: Long,
    val reviewerName: String,
    val reviewerProfileImageUrl: String?,
    val reviewerFishBowlCount: Int, // FIXME: 회원 수조 개수
    val reviewerYears: Int, // FIXME: 회원 가입 연차
) {

    constructor(productReview: ProductReview, reviewer: Member) : this(
        id = productReview.id,
        productId = productReview.productId,
        score = productReview.score.value,
        content = productReview.content,
        createdAt = productReview.createdAt,
        hasPhotos = productReview.hasPhotos,
        recommendCount = productReview.recommendCount,
        reviewerId = reviewer.id,
        reviewerName = reviewer.nickname, // FIXME
        reviewerProfileImageUrl = reviewer.profileImageUrl,
        reviewerFishBowlCount = reviewer.fishBowlCount,
        reviewerYears = reviewer.years,
    )
}

data class ProductReviewScoreWithCount(
    val score: Int,
    val count: Long,
)
