package com.petqua.domain.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationCustomRepository {
    fun countByMemberIdAndIsReadFalse(memberId: Long): Int
}
