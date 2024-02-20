package com.petqua.domain.order

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal

@Embeddable
class OrderProduct(

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false)
    val originalPrice: BigDecimal,

    @Column(nullable = false)
    val discountRate: Int,

    @Column(nullable = false)
    val discountPrice: BigDecimal,

    @Column(nullable = false)
    val deliveryFee: BigDecimal,

    @Column(nullable = false)
    val shippingNumber: String,

    @Column(nullable = false)
    val orderPrice: BigDecimal,

    @Column(nullable = false)
    val productId: Long = 0,

    @Column(nullable = false)
    val productName: String,

    @Column(nullable = false)
    val thumbnailUrl: String,

    @Column(nullable = false)
    val storeId: Long = 0,

    @Column(nullable = false)
    val storeName: String,

    @Column(nullable = false)
    val deliveryMethod: DeliveryMethod,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val sex: Sex,
) {
}
