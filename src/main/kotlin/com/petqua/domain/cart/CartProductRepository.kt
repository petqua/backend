package com.petqua.domain.cart

import com.petqua.domain.delivery.DeliveryMethod
import org.springframework.data.jpa.repository.JpaRepository

interface CartProductRepository : JpaRepository<CartProduct, Long>, CartProductCustomRepository {

    fun findByMemberIdAndProductIdAndIsMaleAndDeliveryMethod(
        memberId: Long,
        productId: Long,
        isMale: Boolean,
        deliveryMethod: DeliveryMethod
    ): CartProduct?

    fun findAllByMemberId(id: Long): List<CartProduct>
}
