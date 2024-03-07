package com.petqua.domain.fish.dto

import io.swagger.v3.oas.annotations.media.Schema

data class SpeciesSearchResponse(
    @Schema(
        description = "어종 이름",
        example = "구피"
    )
    val name: String,
)
