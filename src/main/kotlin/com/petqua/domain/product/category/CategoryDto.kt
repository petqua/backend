package com.petqua.domain.product.category

import com.petqua.domain.product.Sorter
import com.petqua.domain.product.Sorter.NONE

private const val PADDING_FOR_PAGING = 1
private const val LIMIT_CEILING = 20
private const val DEFAULT_LAST_VIEWED_ID = -1L

data class SpeciesResponse(
    val species: String,
)

data class CategoryProductReadCondition(
    val family: String,
    val species: List<String> = listOf(),
    val canDeliverSafely: Boolean? = null,
    val canDeliverCommonly: Boolean? = null,
    val canPickUp: Boolean? = null,
    val sorter: Sorter = NONE,
) {

    companion object {
        fun of(
            family: String,
            species: List<String>,
            canDeliverSafely: Boolean?,
            canDeliverCommonly: Boolean?,
            canPickUp: Boolean?,
            sorter: Sorter,
        ): CategoryProductReadCondition {
            return CategoryProductReadCondition(
                family = family,
                species = species,
                canDeliverSafely = canDeliverSafely,
                canDeliverCommonly = canDeliverCommonly,
                canPickUp = canPickUp,
                sorter = sorter,
            )
        }
    }
}

data class CategoryProductPaging(
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    companion object {
        fun of(lastViewedId: Long?, limit: Int): CategoryProductPaging {
            val adjustedLastViewedId = if (lastViewedId == DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > LIMIT_CEILING) LIMIT_CEILING else limit
            return CategoryProductPaging(
                lastViewedId = adjustedLastViewedId,
                limit = adjustedLimit + PADDING_FOR_PAGING
            )
        }
    }
}
