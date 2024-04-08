package com.petqua.domain.fish

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FishRepository : JpaRepository<Fish, Long>, FishCustomRepository {

    @Query("SELECT COUNT(f) FROM Fish f WHERE f.id IN :ids")
    fun countsByIds(ids: Set<Long>): Int
}
