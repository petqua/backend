package com.petqua.domain.member

import org.springframework.data.jpa.repository.JpaRepository

interface FishTankRepository : JpaRepository<FishTank, Long> {

    fun findByMemberId(memberId: Long): List<FishTank>
}
