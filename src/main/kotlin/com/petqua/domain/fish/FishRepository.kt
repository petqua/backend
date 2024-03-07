package com.petqua.domain.fish

import org.springframework.data.jpa.repository.JpaRepository

interface FishRepository : JpaRepository<Fish, Long>, FishCustomRepository {
}
