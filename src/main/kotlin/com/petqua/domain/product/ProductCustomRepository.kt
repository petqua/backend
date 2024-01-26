package com.petqua.domain.product

import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse

interface ProductCustomRepository {

    fun findAllByCondition(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse>

    fun countByCondition(condition: ProductReadCondition): Int
}
