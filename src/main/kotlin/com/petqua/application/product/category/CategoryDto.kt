package com.petqua.application.product.category

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.Sorter.NONE
import com.petqua.domain.product.category.CategoryProductReadCondition
import com.petqua.domain.product.category.Family

data class CategoryReadQuery(
    val family: String,
) {
    fun toFamily(): Family {
        return Family(family)
    }
}

data class CategoryProductReadQuery(
    val family: String,
    val species: List<String> = listOf(),
    val deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    val sorter: Sorter = NONE,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    fun toCondition(): CategoryProductReadCondition {
        return CategoryProductReadCondition.of(
            family = family,
            species = species,
            deliveryMethod = deliveryMethod,
            sorter = sorter,
        )
    }

    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}
