package com.petqua.application.announcement

import com.petqua.domain.announcement.Announcement
import java.time.LocalDateTime

data class FindAnnouncementResult(
    val id: Long,
    val title: String,
    val linkUrl: String,
    val createAt: LocalDateTime,
    val updateAt: LocalDateTime,
) {
    companion object {
        fun from(announcement: Announcement): FindAnnouncementResult {
            return FindAnnouncementResult(
                id = announcement.id,
                title = announcement.title,
                linkUrl = announcement.linkUrl,
                createAt = announcement.createAt,
                updateAt = announcement.updateAt,
            )
        }
    }
}
