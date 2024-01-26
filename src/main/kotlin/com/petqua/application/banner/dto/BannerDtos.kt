package com.petqua.application.banner.dto

import com.petqua.domain.banner.Banner

data class BannerResponse(
    val id: Long,
    val imageUrl: String,
    val linkUrl: String,
) {
    companion object {
        fun from(banner: Banner): BannerResponse {
            return BannerResponse(
                id = banner.id,
                imageUrl = banner.imageUrl,
                linkUrl = banner.linkUrl,
            )
        }
    }
}
