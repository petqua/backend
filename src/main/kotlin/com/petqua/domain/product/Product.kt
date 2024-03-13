package com.petqua.domain.product

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.Money
import com.petqua.common.domain.SoftDeleteEntity
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.review.ProductReviewStatistics
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_DELIVERY_METHOD
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val categoryId: Long,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "price", nullable = false))
    val price: Money,

    @Column(nullable = false)
    val storeId: Long,

    @Column(nullable = false)
    val discountRate: Int = 0,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "discount_price", nullable = false))
    val discountPrice: Money = price,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "wish_count", nullable = false))
    var wishCount: WishCount = WishCount(),

    @Column(nullable = false)
    val reviewCount: Int = 0,

    @Column(nullable = false)
    val reviewTotalScore: Int = 0,

    @Column(nullable = false)
    val thumbnailUrl: String,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "safe_delivery_fee"))
    val safeDeliveryFee: Money?,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "common_delivery_fee"))
    val commonDeliveryFee: Money?,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "pick_up_delivery_fee"))
    val pickUpDeliveryFee: Money?,

    val productDescriptionId: Long?,

    @Column(nullable = false)
    val productInfoId: Long,
) : BaseEntity(), SoftDeleteEntity {

    fun averageReviewScore(): Double {
        return ProductReviewStatistics.averageReviewScore(reviewTotalScore, reviewCount.toDouble())
    }

    fun increaseWishCount() {
        wishCount = wishCount.increase()
    }

    fun decreaseWishCount() {
        wishCount = wishCount.decrease()
    }

    fun getDeliveryFee(deliveryMethod: DeliveryMethod): Money {
        return when (deliveryMethod) {
            DeliveryMethod.SAFETY -> safeDeliveryFee ?: throw ProductException(INVALID_DELIVERY_METHOD)
            DeliveryMethod.COMMON -> commonDeliveryFee ?: throw ProductException(INVALID_DELIVERY_METHOD)
            DeliveryMethod.PICK_UP -> pickUpDeliveryFee ?: throw ProductException(INVALID_DELIVERY_METHOD)
            else -> throw ProductException(INVALID_DELIVERY_METHOD)
        }
    }

    override fun validateDeleted() {
        if (isDeleted) {
            throw ProductException(NOT_FOUND_PRODUCT)
        }
    }

}
