package com.petqua.domain.fish

import com.petqua.domain.fish.dto.SpeciesSearchResponse

interface FishCustomRepository {

    fun findBySpeciesSearch(species: Species, limit: Int): List<SpeciesSearchResponse>
}
