package com.petqua.domain.order

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.Money
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

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
    val orderShippingAddress: OrderShippingAddress?,

    @Embedded
    val orderProduct: OrderProduct,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "total_amount"))
    val totalAmount: Money,
) : BaseEntity() {

    fun validateAmount(amount: Money) {
        throwExceptionWhen(totalAmount != amount) {
            throw OrderException(PAYMENT_PRICE_NOT_MATCH)
        }
    }

    fun validateOwner(memberId: Long) {
        throwExceptionWhen(this.memberId != memberId) {
            throw OrderException(FORBIDDEN_ORDER)
        }
    }
}
