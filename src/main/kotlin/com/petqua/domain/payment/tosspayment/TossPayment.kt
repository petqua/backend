package com.petqua.domain.payment.tosspayment

import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

// FIXME: 레퍼런스 첨부  https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C
@Entity
class TossPayment(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val paymentKey: String,

    @Embedded
    val orderNumber: OrderNumber,

    @Embedded
    val orderName: OrderName,

    @Enumerated(STRING)
    @Column(nullable = false)
    val method: TossPaymentMethod,

    @Column(nullable = false)
    val totalAmount: String,

    @Enumerated(STRING)
    @Column(nullable = false)
    val status: TossPaymentStatus,

    @Column(nullable = false)
    val requestedAt: String,

    @Column(nullable = false)
    val approvedAt: String,

    @Column(nullable = false)
    val useEscrow: String, // FIXME: 레퍼런스 첨부 https://docs.tosspayments.com/resources/glossary/escrow

    @Enumerated(STRING)
    @Column(nullable = false)
    val type: TossPaymentType,
) {
}
