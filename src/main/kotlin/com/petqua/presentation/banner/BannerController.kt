package com.petqua.presentation.banner

import com.petqua.application.banner.BannerService
import com.petqua.application.banner.dto.BannerResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Banner", description = "배너 관련 API 명세")
@RequestMapping("/banners")
@RestController
class BannerController(
    private val bannerService: BannerService
) {

    @Operation(summary = "배너 조회 API", description = "배너 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "배너 목록 조회 성공")
    @GetMapping
    fun readAll(): ResponseEntity<List<BannerResponse>> {
        val response = bannerService.readAll()
        return ResponseEntity.ok(response)
    }
}
