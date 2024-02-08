package com.petqua.application.product.dto

import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.dto.PRODUCT_LIMIT_CEILING
import com.petqua.domain.product.dto.ProductPaging

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
    val lastViewedId: Long? = null,
    val limit: Int = PRODUCT_LIMIT_CEILING,
) {
    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}
