package com.petqua.application.banner

import com.petqua.application.banner.dto.BannerResponse
import com.petqua.domain.banner.BannerRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class BannerService(
    private val bannerRepository: BannerRepository,
) {

    @Cacheable("banners")
    @Transactional(readOnly = true)
    fun readAll(): List<BannerResponse> {
        val banners = bannerRepository.findAll()
        return banners.map { BannerResponse.from(it) }
    }
}
