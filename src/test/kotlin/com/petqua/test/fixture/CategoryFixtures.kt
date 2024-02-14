package com.petqua.test.fixture

import com.petqua.domain.product.category.Category
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.category.Species

fun category(
    id: Long = 0,
    family: String = "family",
    species: String = "species",
): Category {
    return Category(
        id = id,
        family = Family(family),
        species = Species(species),
    )
}
