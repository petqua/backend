package com.petqua.domain.product.category

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.Sorter.NONE
import io.swagger.v3.oas.annotations.media.Schema

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
