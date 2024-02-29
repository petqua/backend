package com.petqua.domain.cart

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CartProductRepository : JpaRepository<CartProduct, Long>, CartProductCustomRepository {

    fun findByMemberIdAndProductIdAndSexAndDeliveryMethod(
        memberId: Long,
        productId: Long,
        sex: Sex,
        deliveryMethod: DeliveryMethod
    ): CartProduct?

    @Modifying
    @Query("DELETE FROM CartProduct cp WHERE cp.memberId =:memberId")
    fun deleteByMemberId(memberId: Long)
}
