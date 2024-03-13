package com.petqua.domain.notification

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.notification.NotificationException
import com.petqua.exception.notification.NotificationExceptionType.FORBIDDEN_NOTIFICATION
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = false)
    var isRead: Boolean,
) : BaseEntity() {

    fun validateOwner(accessMemberId: Long) {
        throwExceptionWhen(accessMemberId != this.memberId) { NotificationException(FORBIDDEN_NOTIFICATION) }
    }

    fun markAsRead() {
        this.isRead = true
    }
}
