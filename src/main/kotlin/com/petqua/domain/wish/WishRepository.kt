package com.petqua.domain.wish

import org.springframework.data.jpa.repository.JpaRepository

interface WishRepository : JpaRepository<Wish, Long> {

    fun existsByProductIdAndMemberId(productId: Long, memberId: Long): Boolean
}
