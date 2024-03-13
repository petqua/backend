package com.petqua.domain.order

import com.petqua.common.domain.Money
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.math.BigDecimal

@Embeddable
data class OrderProduct(
    @Column(nullable = false)
    val quantity: Int,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "original_price", nullable = false))
    val originalPrice: Money,

    @Column(nullable = false)
    val discountRate: Int,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "discount_price", nullable = false))
    @Column(nullable = false)
    val discountPrice: Money,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "delivery_fee", nullable = false))
    val deliveryFee: Money,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "shippingNumber"))
    var shippingNumber: ShippingNumber,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "order_price", nullable = false))
    val orderPrice: Money,

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
