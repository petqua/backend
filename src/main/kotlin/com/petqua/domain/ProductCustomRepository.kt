package com.petqua.domain

import com.petqua.dto.ProductPaging
import com.petqua.dto.ProductReadCondition
import com.petqua.dto.ProductResponse

interface ProductCustomRepository {

    fun findAllByCondition(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse>

    fun countByCondition(condition: ProductReadCondition): Int
}
