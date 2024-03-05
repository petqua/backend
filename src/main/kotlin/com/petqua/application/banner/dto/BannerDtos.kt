package com.petqua.application.banner.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.petqua.domain.banner.Banner
import io.swagger.v3.oas.annotations.media.Schema

data class BannerResponse(
    @Schema(
        description = "배너 id",
        example = "1"
    )
    @JsonProperty("id")
    val id: Long,

    @Schema(
        description = "이미지 URL",
        example = "image.jpeg"
    )
    @JsonProperty("imageUrl")
    val imageUrl: String,

    @Schema(
        description = "링크 URL",
        example = "link.com"
    )
    @JsonProperty("linkUrl")
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
