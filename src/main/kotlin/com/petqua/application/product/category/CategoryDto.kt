package com.petqua.application.product.category

import com.petqua.domain.product.Sorter
import com.petqua.domain.product.Sorter.NONE
import com.petqua.domain.product.category.CategoryProductPaging
import com.petqua.domain.product.category.CategoryProductReadCondition
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.dto.LIMIT_CEILING

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
    val canDeliverSafely: Boolean? = null,
    val canDeliverCommonly: Boolean? = null,
    val canPickUp: Boolean? = null,
    val sorter: Sorter = NONE,
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {
    fun toCondition(): CategoryProductReadCondition {
        return CategoryProductReadCondition.of(
            family = family,
            species = species,
            canDeliverSafely = canDeliverSafely,
            canDeliverCommonly = canDeliverCommonly,
            canPickUp = canPickUp,
            sorter = sorter,
        )
    }

    fun toPaging(): CategoryProductPaging {
        return CategoryProductPaging.of(
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}
