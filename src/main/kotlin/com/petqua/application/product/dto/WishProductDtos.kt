package com.petqua.application.product.dto

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.product.WishProduct

data class UpdateWishCommand(
    val memberId: Long,
    val productId: Long,
) {
    fun toWishProduct(): WishProduct {
        return WishProduct(
            memberId = memberId,
            productId = productId
        )
    }
}

data class ReadAllWishProductCommand(
    val memberId: Long,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(lastViewedId, limit)
    }
}
