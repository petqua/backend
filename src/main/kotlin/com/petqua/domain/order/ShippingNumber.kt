package com.petqua.domain.order

import com.petqua.domain.delivery.DeliveryMethod
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ShippingNumber(
    @Column(nullable = false, unique = true)
    val value: String,
) {

    companion object {
        fun of(storeId: Long, deliveryMethod: DeliveryMethod, orderNumber: OrderNumber): ShippingNumber {
            return ShippingNumber("${orderNumber.value}${storeId}${deliveryMethod.name}")
        }
    }
}
