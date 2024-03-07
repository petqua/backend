package com.petqua.application.notification

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.notification.NotificationRepository
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
        return notificationRepository.countByMemberIdAndIsReadIsFalse(memberId)
    }

    @CacheEvict(
        key = "'countUnreadNotifications' + #memberId",
        value = ["countUnreadNotifications"]
    )
    fun checkNotification(memberId: Long) {
        // 사용자가 알림을 확인 했을 때, 읽지 않은 알림의 개수 캐싱을 지운다
    }
}
