package com.petqua.domain.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>, NotificationCustomRepository {
    fun countByMemberIdAndIsReadIsFalse(memberId: Long): Int
}

