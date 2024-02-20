package com.petqua.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class OrderShippingAddress(
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
    val requestMessage: String,
) {
}
