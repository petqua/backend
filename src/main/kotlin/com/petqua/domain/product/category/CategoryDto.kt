package com.petqua.domain.product.category

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.Sorter.NONE
import io.swagger.v3.oas.annotations.media.Schema

private const val PADDING_FOR_PAGING = 1
private const val LIMIT_CEILING = 20
private const val DEFAULT_LAST_VIEWED_ID = -1L

data class SpeciesResponse(
    @Schema(
        description = "어종",
        example = "고정구피"
    )
    val species: String,
)

data class CategoryProductReadCondition(
    val family: String,
    val species: List<String> = listOf(),
    val deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    val sorter: Sorter = NONE,
) {

    companion object {
        fun of(
            family: String,
            species: List<String>,
            deliveryMethod: DeliveryMethod,
            sorter: Sorter,
        ): CategoryProductReadCondition {
            return CategoryProductReadCondition(
                family = family,
                species = species,
                deliveryMethod = deliveryMethod,
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
