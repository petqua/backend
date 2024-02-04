package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductSearchCommand
import com.petqua.domain.product.dto.LIMIT_CEILING
import com.petqua.domain.product.dto.ProductPaging

data class ProductSearchRequest(
    val word: String = "",
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }

    fun toCommand(memberId: Long): ProductSearchCommand {
        return ProductSearchCommand(
            memberId = memberId,
            word = word,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}
