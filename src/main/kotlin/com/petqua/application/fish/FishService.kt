package com.petqua.application.fish

import com.petqua.application.fish.dto.SpeciesSearchQuery
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.fish.dto.SpeciesSearchResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class FishService(
    private val fishRepository: FishRepository,
) {

    @Transactional(readOnly = true)
    fun readAutoCompleteSpecies(query: SpeciesSearchQuery): List<SpeciesSearchResponse> {
        val species = query.toSpecies()
        val fishes = fishRepository.findBySpeciesName(species.name, query.limit)
        return fishes.map { SpeciesSearchResponse.from(it) }
    }
}
