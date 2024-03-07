package com.petqua.presentation.notification

import com.petqua.application.notification.NotificationService
import com.petqua.application.notification.dto.ReadAllNotificationResponse
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.notification.dto.ReadAllNotificationRequest
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "알림 관련 API 명세")
@RestController
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        request: ReadAllNotificationRequest,
    ): ReadAllNotificationResponse {
        val query = request.toQuery(loginMember.memberId)
        return notificationService.readAllNotification(query)
    }

    @GetMapping("/unread/count")
    fun countUnread(
        @Auth loginMember: LoginMember,
    ): Int {
        return notificationService.countUnreadNotifications(loginMember.memberId)
    }
}
