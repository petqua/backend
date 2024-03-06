package com.petqua.test.fixture

import com.petqua.domain.notification.Notification

fun notification(
    memberId: Long = 0L,
    title: String = "title",
    content: String = "content",
    isRead: Boolean = false,
): Notification {
    return Notification(
        memberId = memberId,
        title = title,
        content = content,
        isRead = isRead,
    )
}
