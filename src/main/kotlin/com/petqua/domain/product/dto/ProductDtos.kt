package com.petqua.domain.product.dto

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.detail.info.ProductInfo
import com.petqua.domain.product.option.ProductOption
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType
import io.swagger.v3.oas.annotations.media.Schema

data class ProductReadCondition(
    val canDeliverSafely: Boolean? = null,
    val canDeliverCommonly: Boolean? = null,
    val canPickUp: Boolean? = null,
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
) {

    companion object {
        fun toCondition(
            sourceType: ProductSourceType,
            sorter: Sorter
        ): ProductReadCondition {
            return if (sourceType == ProductSourceType.HOME_NEW_ENROLLMENT) ProductReadCondition(
                sourceType = sourceType,
                sorter = Sorter.ENROLLMENT_DATE_DESC
            )
            else ProductReadCondition(
                sourceType = sourceType,
                sorter = sorter
            )
        }
    }
}

data class ProductSearchCondition(
    val word: String,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    val sorter: Sorter = Sorter.NONE,
) {
    companion object {
        fun toCondition(
            word: String,
            deliveryMethod: DeliveryMethod,
            sorter: Sorter
        ): ProductSearchCondition {
            throwExceptionWhen(word.isBlank()) { ProductException(ProductExceptionType.INVALID_SEARCH_WORD) }
            return ProductSearchCondition(
                word = word,
                deliveryMethod = deliveryMethod,
                sorter = sorter
            )
        }
    }
}

data class ProductWithInfoResponse(
    val id: Long,
    val name: String,
    val family: String,
    val species: String,
    val price: Int,
    val storeName: String,
    val discountRate: Int,
    val discountPrice: Int,
    val wishCount: Int,
    val reviewCount: Int,
    val reviewAverageScore: Double,
    val thumbnailUrl: String,
    val descriptionTitle: String,
    val descriptionContent: String,
    val canDeliverSafely: Boolean,
    val canDeliverCommonly: Boolean,
    val canPickUp: Boolean,
    val optimalTemperatureMin: Int,
    val optimalTemperatureMax: Int,
    val difficultyLevel: String,
    val optimalTankSize: String,
    val temperament: String,
    val hasDistinctSex: Boolean,
) {
    constructor(
        product: Product,
        storeName: String,
        productDescription: ProductDescriptionResponse,
        productInfo: ProductInfo,
        category: Category,
        productOption: ProductOption,
    ) : this(
        id = product.id,
        name = product.name,
        family = category.family.name,
        species = category.species.name,
        price = product.price.intValueExact(),
        storeName = storeName,
        discountRate = product.discountRate,
        discountPrice = product.discountPrice.intValueExact(),
        wishCount = product.wishCount.value,
        reviewCount = product.reviewCount,
        reviewAverageScore = product.averageReviewScore(),
        thumbnailUrl = product.thumbnailUrl,
        descriptionTitle = productDescription.title,
        descriptionContent = productDescription.content,
        canDeliverSafely = product.canDeliverSafely,
        canDeliverCommonly = product.canDeliverCommonly,
        canPickUp = product.canPickUp,
        optimalTemperatureMin = productInfo.optimalTemperature.optimalTemperatureMin,
        optimalTemperatureMax = productInfo.optimalTemperature.optimalTemperatureMax,
        difficultyLevel = productInfo.difficultyLevel.description,
        optimalTankSize = productInfo.optimalTankSize.description,
        temperament = productInfo.temperament.description,
        hasDistinctSex = productOption.hasDistinctSex(),
    )
}

data class ProductDescriptionResponse(
    val title: String,
    val content: String,
)

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

    @Schema(
        description = "찜 여부",
        example = "true"
    )
    val isWished: Boolean = false,
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
