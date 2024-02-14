package com.petqua.domain.cart

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.FORBIDDEN_CART_PRODUCT
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class CartProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val productId: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "quantity", nullable = false))
    var quantity: CartProductQuantity,

    @Column(nullable = false)
    var isMale: Boolean,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    var deliveryMethod: DeliveryMethod,
) : BaseEntity() {

    fun validateOwner(accessMemberId: Long) {
        throwExceptionWhen(accessMemberId != this.memberId) { CartProductException(FORBIDDEN_CART_PRODUCT) }
    }

    fun updateOptions(
        quantity: CartProductQuantity,
        isMale: Boolean,
        deliveryMethod: DeliveryMethod,
    ) {
        this.quantity = quantity
        this.isMale = isMale
        this.deliveryMethod = deliveryMethod
    }
}
