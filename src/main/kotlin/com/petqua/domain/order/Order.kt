package com.petqua.domain.order

import com.petqua.common.domain.BaseEntity
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
    val deliveryInfo: OrderShippingAddressInfo,

    @Embedded
    val productInfo: OrderProductInfo,

    @Column(nullable = false)
    val isAbleToCancel: Boolean,

    @Enumerated(STRING)
    @Column(nullable = false)
    val status: OrderStatus,
) : BaseEntity()
