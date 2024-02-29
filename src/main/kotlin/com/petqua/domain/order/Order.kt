package com.petqua.domain.order

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Table(name = "orders")
@Entity
class Order(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "orderNumber"))
    val orderNumber: OrderNumber,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "orderName"))
    val orderName: OrderName,

    @Embedded
    val orderShippingAddress: OrderShippingAddress,

    @Embedded
    val orderProduct: OrderProduct,

    @Column(nullable = false)
    val isAbleToCancel: Boolean,

    @Enumerated(STRING)
    @Column(nullable = false)
    val status: OrderStatus,

    @Column(nullable = false)
    val totalAmount: BigDecimal,
) : BaseEntity() {

    fun validateAmount(amount: BigDecimal) {
        throwExceptionWhen(totalAmount != amount) {
            throw OrderException(PAYMENT_PRICE_NOT_MATCH)
        }
    }
}
