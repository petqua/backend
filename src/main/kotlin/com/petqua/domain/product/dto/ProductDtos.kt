package com.petqua.domain.product.dto

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_SEARCH_WORD
import io.swagger.v3.oas.annotations.media.Schema

const val PRODUCT_PADDING_FOR_PAGING = 1
const val PRODUCT_LIMIT_CEILING = 20
private const val DEFAULT_LAST_VIEWED_ID = -1L

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

data class ProductPaging(
    val lastViewedId: Long? = null,
    val limit: Int = PRODUCT_LIMIT_CEILING,
) {

    companion object {
        fun of(lastViewedId: Long?, limit: Int): ProductPaging {
            val adjustedLastViewedId = if (lastViewedId == DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > PRODUCT_LIMIT_CEILING) PRODUCT_LIMIT_CEILING else limit
            return ProductPaging(adjustedLastViewedId, adjustedLimit + PRODUCT_PADDING_FOR_PAGING)
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
) {
    constructor(product: Product, storeName: String) : this(
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
    )
}
