package com.petqua.domain.product.dto

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_SEARCH_WORD
import io.swagger.v3.oas.annotations.media.Schema

data class ProductReadCondition(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val word: String = "",
    val keyword: String = "",
) {

    companion object {
        fun toCondition(sourceType: ProductSourceType, sorter: Sorter): ProductReadCondition {
            return if (sourceType == ProductSourceType.HOME_NEW_ENROLLMENT) ProductReadCondition(
                sourceType,
                Sorter.ENROLLMENT_DATE_DESC
            )
            else ProductReadCondition(sourceType, sorter)
        }

        fun toSearchCondition(word: String): ProductReadCondition {
            throwExceptionWhen(word.isBlank()) { ProductException(INVALID_SEARCH_WORD) }
            return ProductReadCondition(word = word)
        }
    }
}

data class ProductResponse(
    @Schema(
        description = "상품 Id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "상품 이름",
        example = "알비노 풀레드 아시안 고정구피"
    )
    val name: String,

    @Schema(
        description = "상품 카테고리 id",
        example = "1"
    )
    val categoryId: Long,

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
        description = "안전 배송 가능 여부",
        example = "true"
    )
    val canDeliverSafely: Boolean,

    @Schema(
        description = "일반 배송 가능 여부",
        example = "true"
    )
    val canDeliverCommonly: Boolean,

    @Schema(
        description = "직접 수령 가능 여부",
        example = "true"
    )
    val canPickUp: Boolean,
) {
    constructor(product: Product, storeName: String) : this(
        product.id,
        product.name,
        product.categoryId,
        product.price.intValueExact(),
        storeName,
        product.discountRate,
        product.discountPrice.intValueExact(),
        product.wishCount.value,
        product.reviewCount,
        product.averageReviewScore(),
        product.thumbnailUrl,
        product.canDeliverSafely,
        product.canDeliverCommonly,
        product.canPickUp,
    )
}
