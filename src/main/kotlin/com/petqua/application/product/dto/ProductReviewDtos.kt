package com.petqua.application.product.dto

import com.petqua.domain.product.dto.ProductReviewPaging
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse
import com.petqua.domain.product.dto.REVIEW_PADDING_FOR_PAGING
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import java.time.LocalDateTime

data class ProductReviewReadQuery(
    val productId: Long,
    val memberId: Long?,
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val score: Int?,
    val photoOnly: Boolean = false,
    val lastViewedId: Long,
    val limit: Int,
) {

    fun toCondition(): ProductReviewReadCondition {
        return ProductReviewReadCondition(
            productId = productId,
            sorter = sorter,
            score = score,
            photoOnly = photoOnly,
        )
    }

    fun toPaging(): ProductReviewPaging {
        return ProductReviewPaging.of(lastViewedId, limit)
    }
}

data class ProductReviewsResponse(
    val products: List<ProductReviewResponse>,
    val hasNextPage: Boolean,
) {
    companion object {
        fun of(products: List<ProductReviewResponse>, limit: Int): ProductReviewsResponse {
            return if (products.size > limit) {
                ProductReviewsResponse(products.dropLast(REVIEW_PADDING_FOR_PAGING), hasNextPage = true)
            } else {
                ProductReviewsResponse(products, hasNextPage = false)
            }
        }
    }
}

data class ProductReviewResponse(
    val id: Long,
    val productId: Long,
    val score: Int,
    val content: String,
    val recommendCount: Int,
    val hasPhotos: Boolean,
    val createdAt: LocalDateTime,
    val reviewerId: Long,
    val reviewerName: String,
    val reviewerProfileImageUrl: String?,
    val reviewerFishBowlCount: Int,
    val reviewerYears: Int,
    val recommended: Boolean = false, // TODO: 추천 작업 시 추가 예정
    val images: List<String>,
) {
    constructor(productReviewWithMemberResponse: ProductReviewWithMemberResponse, images: List<String>) : this(
        id = productReviewWithMemberResponse.id,
        productId = productReviewWithMemberResponse.productId,
        score = productReviewWithMemberResponse.score,
        content = productReviewWithMemberResponse.content,
        recommendCount = productReviewWithMemberResponse.recommendCount,
        hasPhotos = productReviewWithMemberResponse.hasPhotos,
        createdAt = productReviewWithMemberResponse.createdAt,
        reviewerId = productReviewWithMemberResponse.reviewerId,
        reviewerName = productReviewWithMemberResponse.reviewerName,
        reviewerProfileImageUrl = productReviewWithMemberResponse.reviewerProfileImageUrl,
        reviewerFishBowlCount = productReviewWithMemberResponse.reviewerFishBowlCount,
        reviewerYears = productReviewWithMemberResponse.reviewerYears,
        images = images,
    )
}
