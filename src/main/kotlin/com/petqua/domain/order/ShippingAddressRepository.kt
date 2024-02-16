package com.petqua.domain.order

import org.springframework.data.jpa.repository.JpaRepository

interface ShippingAddressRepository : JpaRepository<ShippingAddress, Long> {
}
