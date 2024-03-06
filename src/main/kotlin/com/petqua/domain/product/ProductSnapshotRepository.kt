package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductSnapshotRepository : JpaRepository<ProductSnapshot, Long> {

    fun findByProductIdOrderByIdDesc(productId: Long): ProductSnapshot?

    fun findAllByProductIdIn(productIds: List<Long>): List<ProductSnapshot>
}
