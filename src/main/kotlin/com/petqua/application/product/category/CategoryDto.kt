package com.petqua.application.product.category

import com.petqua.domain.product.category.Family

data class CategoryReadQuery(
    val family: String,
) {
    fun toFamily(): Family {
        return Family(family)
    }
}
