package com.petqua.domain.product.category

import com.petqua.domain.product.dto.ProductResponse

interface CategoryCustomRepository {

    fun findProductsByCategoryCondition(
        condition: CategoryProductReadCondition,
        paging: CategoryProductPaging
    ): List<ProductResponse>

    fun countProductsByCategoryCondition(condition: CategoryProductReadCondition): Int
}
