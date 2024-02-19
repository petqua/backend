package com.petqua.application.announcement

import com.fasterxml.jackson.annotation.JsonProperty
import com.petqua.domain.announcement.Announcement
import io.swagger.v3.oas.annotations.media.Schema

data class AnnouncementResponse(
    @Schema(
        description = "배너 id",
        example = "1"
    )
    @JsonProperty("id")
    val id: Long,

    @Schema(
        description = "공지 제목",
        example = "[공지] 펫쿠아 안전 운송 시작"
    )
    @JsonProperty("title")
    val title: String,

    @Schema(
        description = "링크 URL",
        example = "link.com"
    )
    @JsonProperty("linkUrl")
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
