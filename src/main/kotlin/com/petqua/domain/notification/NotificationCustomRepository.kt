package com.petqua.domain.notification

import com.petqua.application.notification.dto.NotificationResponse
import com.petqua.common.domain.dto.CursorBasedPaging

interface NotificationCustomRepository {

    fun findAllByMemberId(memberId: Long, paging: CursorBasedPaging): List<NotificationResponse>
}
