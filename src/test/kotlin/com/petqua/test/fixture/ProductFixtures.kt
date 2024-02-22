package com.petqua.test.fixture

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Product
import com.petqua.domain.product.WishCount
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.detail.description.ProductDescription
import com.petqua.domain.product.detail.description.ProductDescriptionContent
import com.petqua.domain.product.detail.description.ProductDescriptionTitle
import com.petqua.domain.product.detail.image.ImageType
import com.petqua.domain.product.detail.image.ProductImage
import com.petqua.domain.product.detail.info.DifficultyLevel
import com.petqua.domain.product.detail.info.OptimalTankSize
import com.petqua.domain.product.detail.info.OptimalTemperature
import com.petqua.domain.product.detail.info.ProductInfo
import com.petqua.domain.product.detail.info.Temperament
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
    isDeleted: Boolean = false,
    safeDeliveryFee: BigDecimal? = null,
    commonDeliveryFee: BigDecimal? = null,
    pickUpDeliveryFee: BigDecimal? = null,
    productDescriptionId: Long? = null,
    productInfoId: Long = 0,
): Product {
    return Product(
        id = id,
        name = name,
        categoryId = categoryId,
        price = price.setScale(DEFAULT_SCALE),
        storeId = storeId,
        discountRate = discountRate,
        discountPrice = discountPrice.setScale(DEFAULT_SCALE),
        wishCount = WishCount(wishCount),
        reviewCount = reviewCount,
        reviewTotalScore = reviewTotalScore,
        thumbnailUrl = thumbnailUrl,
        isDeleted = isDeleted,
        safeDeliveryFee = safeDeliveryFee?.setScale(DEFAULT_SCALE),
        commonDeliveryFee = commonDeliveryFee?.setScale(DEFAULT_SCALE),
        pickUpDeliveryFee = pickUpDeliveryFee?.setScale(DEFAULT_SCALE),
        productDescriptionId = productDescriptionId,
        productInfoId = productInfoId
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
    categoryId: Long,
    optimalTemperature: OptimalTemperature,
    difficultyLevel: DifficultyLevel,
    optimalTankSize: OptimalTankSize,
    temperament: Temperament,
): ProductInfo {
    return ProductInfo(
        id = id,
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
    imageType: ImageType,
): ProductImage {
    return ProductImage(
        id = id,
        productId = productId,
        imageUrl = imageUrl,
        imageType = imageType,
    )
}

fun productOption(
    id: Long = 0L,
    productId: Long = 0L,
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

fun productDescription(
    id: Long = 0,
    title: String = "제목",
    content: String = "내용",
): ProductDescription {
    return ProductDescription(
        id = id,
        title = ProductDescriptionTitle(title),
        content = ProductDescriptionContent(content)
    )
}

fun productDetailResponse(
    product: Product,
    storeName: String,
    imageUrls: List<String>,
    productDescription: ProductDescription,
    descriptionImageUrls: List<String>,
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
        imageUrls = imageUrls,
        descriptionTitle = productDescription.title.value,
        descriptionContent = productDescription.content.value,
        descriptionImageUrls = descriptionImageUrls,
        safeDeliveryFee = product.safeDeliveryFee,
        commonDeliveryFee = product.commonDeliveryFee,
        pickUpDeliveryFee = product.pickUpDeliveryFee,
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
        safeDeliveryFee = product.safeDeliveryFee,
        commonDeliveryFee = product.commonDeliveryFee,
        pickUpDeliveryFee = product.pickUpDeliveryFee,
        isWished = isWished,
    )
}
