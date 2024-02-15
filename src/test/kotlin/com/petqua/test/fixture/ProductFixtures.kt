package com.petqua.test.fixture

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Product
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.detail.DifficultyLevel
import com.petqua.domain.product.detail.OptimalTankSize
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductImage
import com.petqua.domain.product.detail.ProductInfo
import com.petqua.domain.product.detail.Temperament
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex
import java.math.BigDecimal

private const val DEFAULT_SCALE = 2

fun product(
    id: Long = 0L,
    name: String = "name",
    categoryId: Long = 0,
    price: BigDecimal = BigDecimal.ONE,
    storeId: Long = 0L,
    discountRate: Int = 0,
    discountPrice: BigDecimal = BigDecimal.ONE,
    wishCount: Int = 0,
    reviewCount: Int = 0,
    reviewTotalScore: Int = 0,
    thumbnailUrl: String = "image.jpg",
    description: String = "description",
    isDeleted: Boolean = false,
    canDeliverySafely: Boolean = true,
    canDeliveryCommonly: Boolean = true,
    canPickUp: Boolean = true,
): Product {
    return Product(
        id,
        name,
        categoryId,
        price.setScale(DEFAULT_SCALE),
        storeId,
        discountRate,
        discountPrice.setScale(DEFAULT_SCALE),
        WishCount(wishCount),
        reviewCount,
        reviewTotalScore,
        thumbnailUrl,
        description,
        isDeleted,
        canDeliverySafely,
        canDeliveryCommonly,
        canPickUp,
    )
}

fun productKeyword(
    id: Long = 0L,
    productId: Long = 0L,
    word: String = "word",
): ProductKeyword {
    return ProductKeyword(
        id = id,
        productId = productId,
        word = word
    )
}

fun productInfo(
    id: Long = 0L,
    productId: Long,
    categoryId: Long,
    optimalTemperature: OptimalTemperature,
    difficultyLevel: DifficultyLevel,
    optimalTankSize: OptimalTankSize,
    temperament: Temperament,
): ProductInfo {
    return ProductInfo(
        id = id,
        productId = productId,
        categoryId = categoryId,
        optimalTemperature = optimalTemperature,
        difficultyLevel = difficultyLevel,
        optimalTankSize = optimalTankSize,
        temperament = temperament,
    )
}

fun productImage(
    id: Long = 0,
    productId: Long,
    imageUrl: String,
): ProductImage {
    return ProductImage(
        id = id,
        productId = productId,
        imageUrl = imageUrl,
    )
}

fun productOption(
    id: Long = 0,
    productId: Long,
    sex: Sex,
    additionalPrice: BigDecimal = BigDecimal.ZERO,
): ProductOption {
    return ProductOption(
        id = id,
        productId = productId,
        sex = sex,
        additionalPrice = additionalPrice,
    )
}

fun productDetailResponse(
    product: Product,
    storeName: String,
    imageUrls: List<String>,
    productInfo: ProductInfo,
    category: Category,
    hasDistinctSex: Boolean,
    isWished: Boolean,
): ProductDetailResponse {
    return ProductDetailResponse(
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
        imageUrls = imageUrls,
        description = product.description,
        canDeliverSafely = product.canDeliverSafely,
        canDeliverCommonly = product.canDeliverCommonly,
        canPickUp = product.canPickUp,
        optimalTemperatureMin = productInfo.optimalTemperature.optimalTemperatureMin,
        optimalTemperatureMax = productInfo.optimalTemperature.optimalTemperatureMax,
        difficultyLevel = productInfo.difficultyLevel.description,
        optimalTankSize = productInfo.optimalTankSize.description,
        temperament = productInfo.temperament.description,
        hasDistinctSex = hasDistinctSex,
        isWished = isWished,
    )
}

fun productResponse(
    product: Product,
    storeName: String,
    isWished: Boolean,
): ProductResponse {
    return ProductResponse(
        id = product.id,
        name = product.name,
        categoryId = product.categoryId,
        price = product.price.intValueExact(),
        storeName = storeName,
        discountRate = product.discountRate,
        discountPrice = product.discountPrice.intValueExact(),
        wishCount = product.wishCount.value,
        reviewCount = product.reviewCount,
        reviewAverageScore = product.averageReviewScore(),
        thumbnailUrl = product.thumbnailUrl,
        canDeliverSafely = product.canDeliverSafely,
        canDeliverCommonly = product.canDeliverCommonly,
        canPickUp = product.canPickUp,
        isWished = isWished,
    )
}
