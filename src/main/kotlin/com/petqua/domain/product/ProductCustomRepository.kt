package com.petqua.domain.product

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.product.dto.ProductWithInfoResponse
import com.petqua.domain.product.dto.ProductSearchCondition

interface ProductCustomRepository {

    fun findProductWithInfoByIdOrThrow(id: Long, exceptionSupplier: () -> RuntimeException): ProductWithInfoResponse

    fun findAllByCondition(condition: ProductReadCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun countByReadCondition(condition: ProductReadCondition): Int

    fun findBySearch(condition: ProductSearchCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun countBySearchCondition(condition: ProductSearchCondition): Int

    fun findAllProductResponseByIdIn(ids: List<Long>): List<ProductResponse>

    fun findByKeywordSearch(condition: ProductSearchCondition, paging: CursorBasedPaging): List<ProductResponse>

    fun countByKeywordSearchCondition(condition: ProductSearchCondition): Int
}
