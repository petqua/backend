package com.petqua.application.banner

import com.petqua.domain.banner.Banner
import java.time.LocalDateTime

data class BannerResponse(
    val id: Long,
    val imageUrl: String,
    val linkUrl: String,
    val createAt: LocalDateTime,
    val updateAt: LocalDateTime,
) {
    companion object {
        fun from(banner: Banner): BannerResponse {
            return BannerResponse(
                id = banner.id,
                imageUrl = banner.imageUrl,
                linkUrl = banner.linkUrl,
                createAt = banner.createdAt,
                updateAt = banner.updatedAt,
            )
        }
    }
}
