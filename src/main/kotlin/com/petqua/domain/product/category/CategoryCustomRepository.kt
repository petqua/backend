package com.petqua.domain.product.category

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.product.dto.ProductResponse

interface CategoryCustomRepository {

    fun findProductsByCategoryCondition(
        condition: CategoryProductReadCondition,
        paging: CursorBasedPaging
    ): List<ProductResponse>

    fun countProductsByCategoryCondition(condition: CategoryProductReadCondition): Int
}
