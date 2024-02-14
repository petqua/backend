package com.petqua.test.fixture

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Product
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.detail.DifficultyLevel
import com.petqua.domain.product.detail.OptimalTankSizeLiter
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductImage
import com.petqua.domain.product.detail.ProductInfo
import com.petqua.domain.product.detail.Temperament
import com.petqua.domain.product.dto.ProductResponse
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
    optimalTankSizeLiter: OptimalTankSizeLiter,
    temperament: Temperament,
): ProductInfo {
    return ProductInfo(
        id = id,
        productId = productId,
        categoryId = categoryId,
        optimalTemperature = optimalTemperature,
        difficultyLevel = difficultyLevel,
        optimalTankSizeLiter = optimalTankSizeLiter,
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

fun productDetailResponse(
    product: Product,
    storeName: String,
    imageUrls: List<String>,
    productInfo: ProductInfo,
    isWished: Boolean,
): ProductDetailResponse {
    return ProductDetailResponse(
        id = product.id,
        name = product.name,
        family = "family",
        species = "species",
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
        optimalTankSizeMin = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMin,
        optimalTankSizeMax = productInfo.optimalTankSizeLiter.optimalTankSizeLiterMax,
        temperament = productInfo.temperament.description,
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
