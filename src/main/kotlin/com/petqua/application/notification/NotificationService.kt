package com.petqua.application.notification

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.notification.NotificationRepository
import com.petqua.exception.notification.NotificationException
import com.petqua.exception.notification.NotificationExceptionType.NOTIFICATION_NOT_FOUND
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class NotificationService(
    val notificationRepository: NotificationRepository,
    val memberRepository: MemberRepository,
) {

    @Transactional(readOnly = true)
    fun readAllNotification(query: ReadAllNotificationQuery): ReadAllNotificationResponse {
        val responses = notificationRepository.findAllByMemberId(query.memberId, query.toPaging())
        return ReadAllNotificationResponse.of(responses, query.limit)
    }

    @Cacheable(
        key = "'countUnreadNotifications' + #memberId",
        value = ["countUnreadNotifications"]
    )
    @Transactional(readOnly = true)
    fun countUnreadNotifications(memberId: Long): Int {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId)
    }

    @CacheEvict(
        key = "'countUnreadNotifications' + #memberId",
        value = ["countUnreadNotifications"]
    )
    fun checkNotification(memberId: Long, notificationId: Long) {
        val notification = notificationRepository.findByIdOrThrow(notificationId) {
            NotificationException(NOTIFICATION_NOT_FOUND)
        }
        notification.validateOwner(memberId)
        notification.markAsRead()
    }
}
