package com.petqua.test.fixture

import com.petqua.domain.product.WishProduct

fun wishProduct(
    id: Long = 0L,
    productId: Long = 1L,
    memberId: Long = 1L,
): WishProduct {
    return WishProduct(
        id = id,
        productId = productId,
        memberId = memberId
    )
}
