package com.petqua.application.banner

import com.petqua.domain.banner.Banner
import java.time.LocalDateTime

data class FindBannerResult(
    val id: Long,
    val imageUrl: String,
    val linkUrl: String,
    val createAt: LocalDateTime,
    val updateAt: LocalDateTime,
) {
    companion object {
        fun from(banner: Banner): FindBannerResult {
            return FindBannerResult(
                id = banner.id,
                imageUrl = banner.imageUrl,
                linkUrl = banner.linkUrl,
                createAt = banner.createdAt,
                updateAt = banner.updatedAt,
            )
        }
    }
}
