package com.petqua.application.announcement

import com.petqua.domain.announcement.Announcement

data class AnnouncementResponse(
    val id: Long,
    val title: String,
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
