package com.petqua.domain.product.option

import org.springframework.data.jpa.repository.JpaRepository

interface ProductOptionRepository : JpaRepository<ProductOption, Long> {
    fun findByProductIdIn(productIds: List<Long>): Set<ProductOption>
    fun existsByProductIdAndSex(productId: Long, sex: Sex): Boolean
    fun findAllByProductId(productId: Long): List<ProductOption>
}
