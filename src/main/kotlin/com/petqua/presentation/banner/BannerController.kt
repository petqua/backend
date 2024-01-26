package com.petqua.presentation.banner

import com.petqua.application.banner.BannerService
import com.petqua.application.banner.FindBannerResult
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
    fun getBanners(): ResponseEntity<List<FindBannerResult>> {
        val bannerList = bannerService.getBannerList()
        return ResponseEntity.ok(bannerList)
    }
}
