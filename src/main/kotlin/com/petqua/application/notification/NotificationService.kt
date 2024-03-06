package com.petqua.application.notification

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.notification.NotificationRepository
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
}
