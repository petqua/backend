package com.petqua.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class OrderShippingAddress(
    @Column(nullable = false)
    val receiver: String,

    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = false)
    val zipCode: Int,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val detailAddress: String,
    val requestMessage: String?,
) {
    companion object {
        fun from(shippingAddress: ShippingAddress, requestMessage: String?): OrderShippingAddress {
            return OrderShippingAddress(
                receiver = shippingAddress.receiver,
                phoneNumber = shippingAddress.phoneNumber,
                zipCode = shippingAddress.zipCode,
                address = shippingAddress.address,
                detailAddress = shippingAddress.detailAddress,
                requestMessage = requestMessage,
            )
        }
    }
}
