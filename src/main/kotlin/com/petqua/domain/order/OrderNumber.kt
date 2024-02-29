package com.petqua.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Embeddable
data class OrderNumber(
    @Column(nullable = false, unique = true)
    val value: String,
) {

    companion object {
        fun generate(): OrderNumber { // 202402211607026029E90DB030
            val createdTime = Instant.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
            val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).uppercase()
            return OrderNumber(createdTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + uuid)
        }

        fun from(orderNumber: String): OrderNumber {
            return OrderNumber(orderNumber)
        }
    }
}
