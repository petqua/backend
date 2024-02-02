package com.petqua.domain.cart

import org.springframework.data.jpa.repository.JpaRepository

interface CartProductRepository : JpaRepository<CartProduct, Long> {

    fun findByMemberIdAndProductIdAndIsMaleAndDeliveryMethod(
        memberId: Long,
        productId: Long,
        isMale: Boolean,
        deliveryMethod: DeliveryMethod
    ): CartProduct?

    fun findAllByIdIn(ids: List<Long>): List<CartProduct>
}
