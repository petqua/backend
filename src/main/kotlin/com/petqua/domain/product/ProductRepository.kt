package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>, ProductCustomRepository {

    fun findAllByIsDeletedFalseAndIdIn(ids: List<Long>): Set<Product>
}
