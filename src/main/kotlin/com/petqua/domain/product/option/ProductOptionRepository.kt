package com.petqua.domain.product.option

import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionRepository : JpaRepository<ProductOption, Long> {
    fun findByProductIdIn(productIds: List<Long>): Set<ProductOption>
}
