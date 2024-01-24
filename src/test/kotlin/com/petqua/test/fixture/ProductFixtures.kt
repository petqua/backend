package com.petqua.test.fixture

import com.petqua.domain.Product
import java.math.BigDecimal

private const val DEFAULT_SCALE = 2

fun product(
    id: Long = 0L,
    name: String = "name",
    category: String = "category",
    price: BigDecimal = BigDecimal.ONE,
    storeId: Long = 0L,
    discountRate: Int = 0,
    discountPrice: BigDecimal = BigDecimal.ONE,
    wishCount: Int = 0,
    reviewCount: Int = 0,
    reviewTotalScore: Int = 0,
    thumbnailUrl: String = "image.jpg",
    description: String = "description"
): Product {
    return Product(
        id,
        name,
        category,
        price.setScale(DEFAULT_SCALE),
        storeId,
        discountRate,
        discountPrice.setScale(DEFAULT_SCALE),
        wishCount,
        reviewCount,
        reviewTotalScore,
        thumbnailUrl,
        description
    )
}