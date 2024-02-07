package com.petqua.domain.product.delivery

import com.petqua.common.domain.BaseEntity
import com.petqua.domain.delivery.DeliveryMethod
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class DeliveryProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long = 0L,

    @Enumerated(value = STRING)
    @Column(nullable = false)
    val deliveryMethod: DeliveryMethod,
) : BaseEntity()

