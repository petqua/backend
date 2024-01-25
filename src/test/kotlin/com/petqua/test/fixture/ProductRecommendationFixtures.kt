package com.petqua.test.fixture

import com.petqua.domain.ProductRecommendation

fun productRecommendation(
    id: Long = 0L,
    productId: Long = 0L,
): ProductRecommendation {
    return ProductRecommendation(
        id,
        productId,
    )
}

