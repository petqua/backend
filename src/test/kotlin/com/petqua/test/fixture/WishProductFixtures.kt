package com.petqua.test.fixture

import com.petqua.domain.wish.WishProduct

fun wishProduct(
    id: Long = 0,
    productId: Long = 1,
    memberId: Long = 1,
): WishProduct {
    return WishProduct(
        id = id,
        productId = productId,
        memberId = memberId
    )
}
