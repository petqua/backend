package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WishProductRepository : JpaRepository<WishProduct, Long>, WishProductCustomRepository {

    fun existsByProductIdAndMemberId(productId: Long, memberId: Long): Boolean

    fun findByProductIdAndMemberId(productId: Long, memberId: Long): WishProduct?

    fun countByMemberId(memberId: Long): Int

    @Query("SELECT w.productId FROM WishProduct w WHERE w.memberId = :memberId AND w.productId IN :productIds")
    fun findWishedProductIdByMemberIdAndProductIdIn(memberId: Long, productIds: List<Long>): List<Long>
}
