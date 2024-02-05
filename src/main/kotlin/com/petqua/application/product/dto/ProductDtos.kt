package com.petqua.application.product.dto

import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING
import com.petqua.domain.product.dto.PADDING_FOR_PAGING
import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import io.swagger.v3.oas.annotations.media.Schema

data class ProductDetailResponse(
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
        description = "상품 상세 설명",
        example = "귀엽습니다"
    )
    val description: String,
) {
    constructor(product: Product, storeName: String, reviewAverageScore: Double) : this(
        product.id,
        product.name,
        product.category,
        product.price.intValueExact(),
        storeName,
        product.discountRate,
        product.discountPrice.intValueExact(),
        product.wishCount.value,
        product.reviewCount,
        reviewAverageScore,
        product.thumbnailUrl,
        product.description,
    )
}

data class ProductReadQuery(
    @Schema(
        description = "상품 조회 출처",
        example = "HOME_RECOMMENDED",
        allowableValues = ["HOME_RECOMMENDED", "HOME_NEW_ENROLLMENT"]
    )
    val sourceType: ProductSourceType = ProductSourceType.NONE,

    @Schema(
        description = "정렬 기준",
        defaultValue = "ENROLLMENT_DATE_DESC",
        allowableValues = ["SALE_PRICE_ASC", "SALE_PRICE_DESC", "REVIEW_COUNT_DESC", "ENROLLMENT_DATE_DESC"]
    )
    val sorter: Sorter = Sorter.NONE,

    @Schema(
        description = "마지막으로 조회한 상품의 Id",
        example = "1"
    )
    val lastViewedId: Long? = null,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = LIMIT_CEILING,
) {
    fun toReadConditions(): ProductReadCondition {
        return ProductReadCondition.toCondition(sourceType, sorter)
    }

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}

data class ProductsResponse(
    val products: List<ProductResponse>,

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
        fun of(products: List<ProductResponse>, limit: Int, totalProductsCount: Int): ProductsResponse {
            return if (products.size > limit) {
                ProductsResponse(products.dropLast(PADDING_FOR_PAGING), hasNextPage = true, totalProductsCount)
            } else {
                ProductsResponse(products, hasNextPage = false, totalProductsCount)
            }
        }
    }
}

data class ProductSearchQuery(
    val word: String = "",
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toSearchCondition(): ProductReadCondition {
        return ProductReadCondition.toSearchCondition(word)
    }

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}

data class ProductKeywordQuery(
    val word: String = "",
    val limit: Int = LIMIT_CEILING,
) {

    fun toProductKeyword(): ProductKeyword {
        return ProductKeyword(word = word)
    }
}

data class ProductKeywordResponse(
    val keyword: String,
)
