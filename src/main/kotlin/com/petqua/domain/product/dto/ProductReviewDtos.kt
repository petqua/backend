package com.petqua.domain.product.dto

import com.petqua.domain.member.Member
import com.petqua.domain.product.review.ProductReview
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import java.time.LocalDateTime

const val REVIEW_PADDING_FOR_PAGING = 1
const val REVIEW_LIMIT_CEILING = 20
private const val REVIEW_DEFAULT_LAST_VIEWED_ID = -1L

data class ProductReviewReadCondition(
    val productId: Long,
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val score: Int? = null,
    val photoOnly: Boolean,
) {
}

data class ProductReviewPaging(
    val lastViewedId: Long? = null,
    val limit: Int,
) {

    companion object {
        fun of(lastViewedId: Long?, limit: Int): ProductReviewPaging {
            val adjustedLastViewedId = if (lastViewedId == REVIEW_DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > REVIEW_LIMIT_CEILING) REVIEW_LIMIT_CEILING else limit
            return ProductReviewPaging(adjustedLastViewedId, adjustedLimit + REVIEW_PADDING_FOR_PAGING)
        }
    }
}

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
        score = productReview.score,
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
