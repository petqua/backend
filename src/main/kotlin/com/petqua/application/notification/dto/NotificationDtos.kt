package com.petqua.application.notification.dto

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.notification.Notification
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class ReadAllNotificationResponse(
    val notifications: List<NotificationResponse>,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
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
    @Schema(
        description = "알림 Id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "회원 Id",
        example = "1"
    )
    val memberId: Long,

    @Schema(
        description = "알림 제목",
        example = "알림 제목"
    )
    val title: String,

    @Schema(
        description = "알림 내용",
        example = "알림 내용"
    )
    val content: String,

    @Schema(
        description = "알림 읽음 여부",
        example = "false"
    )
    val isRead: Boolean,

    @Schema(
        description = "알림 생성일",
        example = "2024-03-07T00:04:21"
    )
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
