package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductReadCommand
import com.petqua.application.product.dto.ProductSearchCommand
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING

data class ProductSearchRequest(
    val word: String = "",
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toCommand(memberId: Long): ProductSearchCommand {
        return ProductSearchCommand(
            word = word,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}

data class ProductReadRequest(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toCommand(memberId: Long): ProductReadCommand {
        return ProductReadCommand(
            sourceType = sourceType,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}
