package com.petqua.application.notification.dto

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.notification.Notification
import java.time.LocalDateTime

data class ReadAllNotificationResponse(
    val notifications: List<NotificationResponse>,
    val hasNextPage: Boolean,
) {
    companion object {
        fun of(notificationResponses: List<NotificationResponse>, limit: Int): ReadAllNotificationResponse {
            return if (notificationResponses.size > limit) {
                ReadAllNotificationResponse(
                    notificationResponses.dropLast(PADDING_FOR_HAS_NEXT_PAGE),
                    hasNextPage = true,
                )
            } else {
                ReadAllNotificationResponse(notificationResponses, hasNextPage = false)
            }
        }
    }
}

data class NotificationResponse(
    val id: Long,
    val memberId: Long,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
) {
    constructor(notification: Notification) : this(
        id = notification.id,
        memberId = notification.memberId,
        title = notification.title,
        content = notification.content,
        isRead = notification.isRead,
        createdAt = notification.createdAt,
    )
}

data class ReadAllNotificationQuery(
    val memberId: Long,
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toPaging(): CursorBasedPaging {
        return CursorBasedPaging.of(lastViewedId, limit)
    }
}
