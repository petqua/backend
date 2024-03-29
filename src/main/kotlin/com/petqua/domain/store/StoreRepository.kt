package com.petqua.domain.store

import org.springframework.data.jpa.repository.JpaRepository

interface StoreRepository : JpaRepository<Store, Long> {
    fun findByIdIn(ids: List<Long>): Set<Store>
}
