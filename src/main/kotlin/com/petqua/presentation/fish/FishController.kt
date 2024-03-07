package com.petqua.presentation.fish

import com.petqua.application.fish.FishService
import com.petqua.domain.fish.dto.SpeciesSearchResponse
import com.petqua.presentation.fish.dto.SpeciesSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Fish", description = "물고기 관련 API 명세")
@RestController
@RequestMapping("/fishes")
class FishController(
    private val fishService: FishService,
) {

    @Operation(summary = "어종 검색어(자동완성) 조회 API", description = "어종 검색어를 조회합니다")
    @ApiResponse(responseCode = "200", description = "어종 검색어 조회 성공")
    @GetMapping("/species")
    fun readAutoCompleteSpecies(
        request: SpeciesSearchRequest,
    ): ResponseEntity<List<SpeciesSearchResponse>> {
        val response = fishService.readAutoCompleteSpecies(request.toQuery())
        return ResponseEntity.ok(response)
    }
}
