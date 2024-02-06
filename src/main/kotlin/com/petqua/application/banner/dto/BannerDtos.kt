package com.petqua.application.banner.dto

import com.petqua.domain.banner.Banner
import io.swagger.v3.oas.annotations.media.Schema

data class BannerResponse(
    @Schema(
        description = "배너 id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "이미지 URL",
        example = "image.jpeg"
    )
    val imageUrl: String,

    @Schema(
        description = "링크 URL",
        example = "link.com"
    )
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
