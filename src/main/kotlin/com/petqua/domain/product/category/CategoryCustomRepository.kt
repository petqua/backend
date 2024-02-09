package com.petqua.domain.product.category

import com.petqua.domain.product.dto.ProductResponse

interface CategoryCustomRepository {

    fun findProductsByCondition(
        condition: CategoryProductReadCondition,
        paging: CategoryProductPaging
    ): List<ProductResponse>
}
