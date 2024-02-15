package com.petqua.test.fixture

import com.petqua.domain.product.WishProduct

fun wishProduct(
    id: Long = 0L,
    productId: Long = 0L,
    memberId: Long = 0L,
): WishProduct {
    return WishProduct(
        id = id,
        productId = productId,
        memberId = memberId
    )
}
