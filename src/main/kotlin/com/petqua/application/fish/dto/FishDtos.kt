package com.petqua.application.fish.dto

import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.fish.Species

data class SpeciesSearchQuery(
    val word: String,
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    fun toSpecies(): Species {
        return Species.from(word)
    }
}
