package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductSnapshotRepository : JpaRepository<ProductSnapshot, Long> {

    fun findByProductIdOrderByIdDesc(productId: Long): ProductSnapshot?

    @Query("SELECT ps FROM ProductSnapshot ps WHERE ps.id IN (SELECT MAX(ps2.id) FROM ProductSnapshot ps2 WHERE ps2.productId IN :productIds GROUP BY ps2.productId)")
    fun findAllByProductIdIn(productIds: List<Long>): List<ProductSnapshot>
}
