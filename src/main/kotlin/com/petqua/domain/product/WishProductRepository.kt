package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository

interface WishProductRepository : JpaRepository<WishProduct, Long>, WishProductCustomRepository {

    fun existsByProductIdAndMemberId(productId: Long, memberId: Long): Boolean

    fun findByProductIdAndMemberId(productId: Long, memberId: Long): WishProduct?

    fun countByMemberId(memberId: Long): Int
}
