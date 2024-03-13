package com.petqua.presentation.notification.dto

import com.petqua.application.notification.dto.ReadAllNotificationQuery
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import io.swagger.v3.oas.annotations.media.Schema

data class ReadAllNotificationRequest(
    @Schema(
        description = "마지막으로 조회한 알림의 Id",
        example = "1"
    )
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,

    @Schema(
        description = "조회할 알림 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {
    
    fun toQuery(memberId: Long): ReadAllNotificationQuery {
        return ReadAllNotificationQuery(
            memberId = memberId,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}
