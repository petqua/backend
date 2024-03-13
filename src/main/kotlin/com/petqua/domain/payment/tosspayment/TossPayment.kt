package com.petqua.domain.payment.tosspayment

import com.petqua.common.domain.Money
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class TossPayment(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val paymentKey: String,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "order_number", nullable = false, unique = true))
    val orderNumber: OrderNumber,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "order_name", nullable = false))
    val orderName: OrderName,

    @Enumerated(STRING)
    @Column(nullable = false)
    val method: TossPaymentMethod,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "total_amount", nullable = false))
    val totalAmount: Money,

    @Enumerated(STRING)
    @Column(nullable = false)
    val status: TossPaymentStatus,

    @Column(nullable = false)
    val requestedAt: String,

    @Column(nullable = false)
    val approvedAt: String,

    @Column(nullable = false)
    val useEscrow: Boolean,

    @Enumerated(STRING)
    @Column(nullable = false)
    val type: TossPaymentType,
) {
}
