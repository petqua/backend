package com.petqua.domain.product

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.SoftDeleteEntity
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal
import java.math.RoundingMode

private const val SCALE = 1
private const val ZERO = 0

@Entity
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val categoryId: Long = 0,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = false)
    val storeId: Long = 0,

    @Column(nullable = false)
    val discountRate: Int = 0,

    @Column(nullable = false)
    val discountPrice: BigDecimal = price,

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
    val description: String,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    @Column(nullable = false)
    val canDeliverSafely: Boolean,

    @Column(nullable = false)
    val canDeliverCommonly: Boolean,

    @Column(nullable = false)
    val canPickUp: Boolean,
) : BaseEntity(), SoftDeleteEntity {

    fun averageReviewScore(): Double {
        return if (reviewCount == ZERO) ZERO.toDouble()
        else BigDecimal.valueOf(reviewTotalScore / reviewCount.toDouble())
            .setScale(SCALE, RoundingMode.HALF_UP)
            .toDouble()
    }

    fun increaseWishCount() {
        wishCount = wishCount.increase()
    }

    fun decreaseWishCount() {
        wishCount = wishCount.decrease()
    }

    override fun validateDeleted() {
        if (isDeleted) {
            throw ProductException(NOT_FOUND_PRODUCT)
        }
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name', categoryId=$categoryId, price=$price, storeId=$storeId, discountRate=$discountRate, discountPrice=$discountPrice, wishCount=$wishCount, reviewCount=$reviewCount, reviewTotalScore=$reviewTotalScore, thumbnailUrl='$thumbnailUrl', description='$description', isDeleted=$isDeleted, canDeliverSafely=$canDeliverSafely, canDeliverCommonly=$canDeliverCommonly, canPickUp=$canPickUp)"
    }
}
