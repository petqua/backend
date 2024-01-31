package com.petqua.domain.cart

import org.springframework.data.jpa.repository.JpaRepository

interface CartProductRepository : JpaRepository<CartProduct, Long> {

    fun findByMemberIdAndProductIdAndMaleAndDeliveryMethod(
        memberId: Long,
        productId: Long,
        isMale: Boolean,
        deliveryMethod: DeliveryMethod
    ): CartProduct?
}
