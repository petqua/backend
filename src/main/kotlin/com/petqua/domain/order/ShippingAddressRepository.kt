package com.petqua.domain.order

import org.springframework.data.jpa.repository.JpaRepository

interface ShippingAddressRepository : JpaRepository<ShippingAddress, Long> {

    fun findByMemberIdAndIsDefaultAddress(memberId: Long, isDefaultAddress: Boolean = true): ShippingAddress?

    fun deleteByMemberIdAndIsDefaultAddress(memberId: Long, isDefaultAddress: Boolean = true)
}
