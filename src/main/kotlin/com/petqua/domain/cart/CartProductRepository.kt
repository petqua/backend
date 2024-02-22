package com.petqua.domain.cart

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import org.springframework.data.jpa.repository.JpaRepository

interface CartProductRepository : JpaRepository<CartProduct, Long>, CartProductCustomRepository {

    fun findByMemberIdAndProductIdAndSexAndDeliveryMethod(
        memberId: Long,
        productId: Long,
        sex: Sex,
        deliveryMethod: DeliveryMethod
    ): CartProduct?

    fun findAllByMemberId(id: Long): List<CartProduct>
}
