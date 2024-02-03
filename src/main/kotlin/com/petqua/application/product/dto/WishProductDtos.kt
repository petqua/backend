package com.petqua.application.product.dto

import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.dto.LIMIT_CEILING
import com.petqua.domain.product.dto.ProductPaging

data class SaveWishCommand(
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

data class DeleteWishCommand(
    val memberId: Long,
    val wishProductId: Long,
)

data class ReadAllWishProductCommand(
    val memberId: Long,
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {
    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}
