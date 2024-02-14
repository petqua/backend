package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.UpdateWishCommand
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.product.Product
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateWishRequest(
    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,
) {
    fun toCommand(memberId: Long): UpdateWishCommand {
        return UpdateWishCommand(
            memberId = memberId,
            productId = productId
        )
    }
}

data class ReadAllWishProductRequest(
    @Schema(
        description = "마지막으로 조회한 찜의 Id",
        example = "1"
    )
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,

    @Schema(
        description = "조회할 찜 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toCommand(memberId: Long): ReadAllWishProductCommand {
        return ReadAllWishProductCommand(
            memberId = memberId,
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}

data class WishProductsResponse(
    val products: List<WishProductResponse>,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
    val hasNextPage: Boolean,

    @Schema(
        description = "조회 조건에 해당하는 전체 상품 개수",
        example = "50"
    )
    val totalProductsCount: Int,
) {
    companion object {
        fun of(wishProducts: List<WishProductResponse>, limit: Int, totalWishProductsCount: Int): WishProductsResponse {
            return if (wishProducts.size > limit) {
                WishProductsResponse(
                    wishProducts.dropLast(PADDING_FOR_HAS_NEXT_PAGE),
                    hasNextPage = true,
                    totalWishProductsCount
                )
            } else {
                WishProductsResponse(wishProducts, hasNextPage = false, totalWishProductsCount)
            }
        }
    }
}

data class WishProductResponse(
    @Schema(
        description = "찜 Id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "상품 Id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상품 이름",
        example = "알비노 풀레드 아시안 고정구피"
    )
    val name: String,

    @Schema(
        description = "상품 카테고리",
        example = "난태생, 송사리과"
    )
    val category: String,

    @Schema(
        description = "상품 가격",
        example = "30000"
    )
    val price: Int,

    @Schema(
        description = "상품 판매점",
        example = "S아쿠아"
    )
    val storeName: String,

    @Schema(
        description = "가격 할인율",
        example = "30"
    )
    val discountRate: Int,

    @Schema(
        description = "할인 가격(판매 가격)",
        example = "21000"
    )
    val discountPrice: Int,

    @Schema(
        description = "찜 개수",
        example = "23"
    )
    val wishCount: Int,

    @Schema(
        description = "리뷰 개수",
        example = "50"
    )
    val reviewCount: Int,

    @Schema(
        description = "리뷰 평균 점수",
        example = "5"
    )
    val reviewAverageScore: Double,

    @Schema(
        description = "상품 썸네일 이미지",
        example = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
    )
    val thumbnailUrl: String,

    @Schema(
        description = "상품 삭제 여부",
        example = "false"
    )
    val isDeletedProduct: Boolean,
) {
    constructor(wishProductId: Long, product: Product, storeName: String) : this(
        wishProductId,
        product.id,
        product.name,
        product.category,
        product.price.intValueExact(),
        storeName,
        product.discountRate,
        product.discountPrice.intValueExact(),
        product.wishCount.value,
        product.reviewCount,
        product.averageReviewScore(),
        product.thumbnailUrl,
        product.isDeleted,
    )
}
