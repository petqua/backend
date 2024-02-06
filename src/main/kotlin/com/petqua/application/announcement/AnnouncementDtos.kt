package com.petqua.application.announcement

import com.petqua.domain.announcement.Announcement
import io.swagger.v3.oas.annotations.media.Schema

data class AnnouncementResponse(
    @Schema(
        description = "배너 id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "공지 제목",
        example = "[공지] 펫쿠아 안전 운송 시작"
    )
    val title: String,

    @Schema(
        description = "링크 URL",
        example = "link.com"
    )
    val linkUrl: String,
) {
    companion object {
        fun from(announcement: Announcement): AnnouncementResponse {
            return AnnouncementResponse(
                id = announcement.id,
                title = announcement.title,
                linkUrl = announcement.linkUrl,
            )
        }
    }
}
