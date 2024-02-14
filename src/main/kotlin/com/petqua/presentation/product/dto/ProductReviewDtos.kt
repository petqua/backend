package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductReviewReadQuery
import com.petqua.application.product.dto.UpdateReviewRecommendationCommand
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import io.swagger.v3.oas.annotations.media.Schema

data class ReadAllProductReviewsRequest(
    @Schema(
        description = "상품 후기 정렬 기준",
        example = "REVIEW_DATE_DESC",
        allowableValues = ["REVIEW_DATE_DESC", "RECOMMEND_DESC"]
    )
    val sorter: ProductReviewSorter = REVIEW_DATE_DESC,

    @Schema(
        description = "마지막으로 조회한 후기의 Id",
        example = "1",
    )
    val lastViewedId: Long,

    @Schema(
        description = "사진 후기만 조회 여부",
        example = "false",
    )
    val photoOnly: Boolean = false,

    @Schema(
        description = "조회할 후기의 평점",
        example = "5",
        nullable = true,
    )
    val score: Int? = null,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    fun toCommand(productId: Long, memberId: Long?): ProductReviewReadQuery {
        return ProductReviewReadQuery(
            productId = productId,
            memberId = memberId,
            sorter = sorter,
            score = score,
            photoOnly = photoOnly,
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}

data class UpdateReviewRecommendationRequest(
    @Schema(
        description = "상품 후기 id",
        example = "1"
    )
    val productReviewId: Long,
) {
    fun toCommand(memberId: Long): UpdateReviewRecommendationCommand {
        return UpdateReviewRecommendationCommand(
            memberId = memberId,
            productReviewId = productReviewId
        )
    }
}
