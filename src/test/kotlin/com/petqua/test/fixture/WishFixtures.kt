package com.petqua.test.fixture

import com.petqua.domain.wish.Wish

fun wish(
    id: Long = 0,
    productId: Long = 1,
    memberId: Long = 1,
): Wish {
    return Wish(
        id = id,
        productId = productId,
        memberId = memberId
    )
}
