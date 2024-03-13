package com.petqua.domain.fish.dto

import com.petqua.domain.fish.Fish
import io.swagger.v3.oas.annotations.media.Schema

data class SpeciesSearchResponse(
    @Schema(
        description = "어종 id",
        example = "1"
    )
    val fishId: Long,

    @Schema(
        description = "어종 이름",
        example = "구피"
    )
    val name: String,
) {

    companion object {
        fun from(fish: Fish): SpeciesSearchResponse {
            return SpeciesSearchResponse(fish.id, fish.species.name)
        }
    }
}
