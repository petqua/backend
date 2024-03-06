package com.petqua.domain.order

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.regex.Pattern

@Embeddable
data class OrderNumber(
    @Column(nullable = false, unique = true)
    val value: String,
) {

    companion object {
        private val orderNumberPattern = Pattern.compile("^\\d{14}[A-Z0-9]{12}\$")  // 숫자 14자 + 숫자 or 대문자 12자

        fun generate(): OrderNumber { // 202402211607026029E90DB030
            val createdTime = Instant.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
            val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).uppercase()
            return OrderNumber(createdTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + uuid)
        }

        fun from(orderNumber: String): OrderNumber {
            throwExceptionWhen(!orderNumberPattern.matcher(orderNumber).matches()) {
                OrderException(ORDER_NOT_FOUND)
            }
            return OrderNumber(orderNumber)
        }
    }
}
