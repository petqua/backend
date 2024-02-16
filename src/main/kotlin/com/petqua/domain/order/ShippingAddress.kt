package com.petqua.domain.order

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType.INVALID_PHONE_NUMBER
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class ShippingAddress(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long = 0L,

    @Column(nullable = false)
    val name: String,

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

    @Column(nullable = false)
    val isDefaultAddress: Boolean = false,
) : BaseEntity() {

    init {
        throwExceptionWhen(!isValidatePhoneNumber()) { ShippingAddressException(INVALID_PHONE_NUMBER) }
    }

    private fun isValidatePhoneNumber(): Boolean {
        val regex = "^010-\\d{4}-\\d{4}$".toRegex()
        return phoneNumber.matches(regex)
    }
}
