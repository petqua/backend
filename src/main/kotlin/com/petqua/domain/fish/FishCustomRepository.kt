package com.petqua.domain.fish

interface FishCustomRepository {

    fun findBySpeciesName(speciesName: String, limit: Int): List<Fish>
}
