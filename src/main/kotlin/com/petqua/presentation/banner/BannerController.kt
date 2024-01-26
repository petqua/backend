package com.petqua.presentation.banner

import com.petqua.application.banner.BannerService
import com.petqua.application.banner.dto.BannerResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/banners")
@RestController
class BannerController(
    private val bannerService: BannerService
) {

    @GetMapping
    fun readAll(): ResponseEntity<List<BannerResponse>> {
        val response = bannerService.readAll()
        return ResponseEntity.ok(response)
    }
}
