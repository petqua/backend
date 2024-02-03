package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING

data class ProductReadRequest(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toQuery(): ProductReadQuery {
        return ProductReadQuery(
            sourceType = sourceType,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}

data class ProductSearchRequest(
    val word: String = "",
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toQuery(): ProductSearchQuery {
        return ProductSearchQuery(
            word = word,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}

data class ProductKeywordRequest(
    val word: String = "",
    val limit: Int = LIMIT_CEILING,
) {

    fun toQuery(): ProductKeywordQuery {
        return ProductKeywordQuery(
            word = word,
            limit = limit,
        )
    }
}
