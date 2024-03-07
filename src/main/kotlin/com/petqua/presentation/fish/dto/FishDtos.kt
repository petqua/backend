package com.petqua.presentation.fish.dto

import com.petqua.application.fish.dto.SpeciesSearchQuery
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import io.swagger.v3.oas.annotations.media.Schema

data class SpeciesSearchRequest(
    @Schema(
        description = "검색어",
        example = "베"
    )
    val word: String,

    @Schema(
        description = "조회할 어종 개수",
        defaultValue = "5"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    fun toQuery(): SpeciesSearchQuery {
        return SpeciesSearchQuery(
            word = word,
            limit = limit,
        )
    }
}
