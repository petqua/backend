package com.petqua.domain.cart

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class CartProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val productId: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "quantity", nullable = false))
    val quantity: CartProductQuantity,

    @Column(nullable = false)
    val isMale: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val deliveryMethod: DeliveryMethod,
) : BaseEntity()
