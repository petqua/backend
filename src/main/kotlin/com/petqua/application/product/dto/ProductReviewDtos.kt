package com.petqua.application.product.dto

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import com.petqua.domain.product.review.ProductReviewStatistics
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class ProductReviewReadQuery(
    val productId: Long,
    val loginMemberOrGuest: LoginMemberOrGuest,
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    val score: Int? = null,
    val photoOnly: Boolean = false,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toCondition(): ProductReviewReadCondition {
        return ProductReviewReadCondition(
            productId = productId,
            sorter = sorter,
            score = score,
            photoOnly = photoOnly,
        )
    }

    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(lastViewedId, limit)
    }
}

data class ProductReviewsResponse(
    val productReviews: List<ProductReviewResponse>,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
    val hasNextPage: Boolean,
) {
    companion object {
        fun of(products: List<ProductReviewResponse>, limit: Int): ProductReviewsResponse {
            return if (products.size > limit) {
                ProductReviewsResponse(products.dropLast(PADDING_FOR_HAS_NEXT_PAGE), hasNextPage = true)
            } else {
                ProductReviewsResponse(products, hasNextPage = false)
            }
        }
    }
}

data class ProductReviewResponse(
    @Schema(
        description = "상품 후기의 id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상품 후기 평점",
        example = "5"
    )
    val score: Int,

    @Schema(
        description = "상품 후기 내용",
        example = "아주 좋네요"
    )
    val content: String,

    @Schema(
        description = "상품 후기 추천 수",
        example = "3"
    )
    val recommendCount: Int,

    @Schema(
        description = "상품 후기에 사진이 있는지 여부",
        example = "true"
    )
    val hasPhotos: Boolean,

    @Schema(
        description = "상품 후기 작성일",
        example = "2021-08-01T00:00:00"
    )
    val createdAt: LocalDateTime,

    @Schema(
        description = "상품 후기 작성자 id",
        example = "1"
    )
    val reviewerId: Long,

    @Schema(
        description = "상품 후기 작성자 이름",
        example = "홍길동"
    )
    val reviewerName: String,

    @Schema(
        description = "상품 후기 작성자 프로필 이미지 url",
        example = "http:/docs.petqua.co.kr/profile.jpg"
    )
    val reviewerProfileImageUrl: String?,

    @Schema(
        description = "상품 후기 작성자 수조 개수",
        example = "3"
    )
    val reviewerFishBowlCount: Int,

    @Schema(
        description = "상품 후기 작성자 회원 가입 연차",
        example = "3"
    )
    val reviewerYears: Int,

    @Schema(
        description = "상품 후기 추천 여부",
        example = "true"
    )
    val recommended: Boolean = false, // TODO: 추천 작업 시 추가 예정

    @Schema(
        description = "상품 후기에 등록된 이미지 url 리스트",
        example = "[\"http:/docs.petqua.co.kr/review1.jpg\", \"http:/docs.petqua.co.kr/review2.jpg\"]"
    )
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
        reviewerFishBowlCount = productReviewWithMemberResponse.reviewerFishTankCount,
        reviewerYears = productReviewWithMemberResponse.reviewerYears,
        images = images,
    )
}

data class ProductReviewStatisticsResponse(
    @Schema(
        description = "별점 5의 개수",
        example = "3"
    )
    val scoreFiveCount: Int,

    @Schema(
        description = "별점 4의 개수",
        example = "0"
    )
    val scoreFourCount: Int,

    @Schema(
        description = "별점 3의 개수",
        example = "0"
    )
    val scoreThreeCount: Int,

    @Schema(
        description = "별점 2의 개수",
        example = "2"
    )
    val scoreTwoCount: Int,

    @Schema(
        description = "별점 1의 개수",
        example = "0"
    )
    val scoreOneCount: Int,

    @Schema(
        description = "만족도",
        example = "60"
    )
    val productSatisfaction: Int,

    @Schema(
        description = "후기 총 개수",
        example = "5"
    )
    val totalReviewCount: Int,

    @Schema(
        description = "평균 별점",
        example = "3.8"
    )
    val averageScore: Double,
) {

    companion object {
        fun from(countsByScores: ProductReviewStatistics): ProductReviewStatisticsResponse {
            return ProductReviewStatisticsResponse(
                scoreFiveCount = countsByScores.scoreFiveCount,
                scoreFourCount = countsByScores.scoreFourCount,
                scoreThreeCount = countsByScores.scoreThreeCount,
                scoreTwoCount = countsByScores.scoreTwoCount,
                scoreOneCount = countsByScores.scoreOneCount,
                productSatisfaction = countsByScores.productSatisfaction,
                totalReviewCount = countsByScores.totalReviewCount,
                averageScore = countsByScores.averageScore,
            )
        }
    }
}
