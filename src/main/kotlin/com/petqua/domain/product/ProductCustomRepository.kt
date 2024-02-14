package com.petqua.domain.product

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse

interface ProductCustomRepository {

    fun findAllByCondition(condition: ProductReadCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun countByCondition(condition: ProductReadCondition): Int

    fun findBySearch(condition: ProductReadCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun findAllProductResponseByIdIn(ids: List<Long>): List<ProductResponse>

    fun findByKeywordSearch(condition: ProductReadCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun countByKeywordCondition(condition: ProductReadCondition): Int
}
