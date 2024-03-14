package com.petqua.domain.product

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.Money
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ProductSnapshot(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long = 0L,

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

    @Column(nullable = false)
    val thumbnailUrl: String,

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
) : BaseEntity() {

    companion object {
        fun from(product: Product): ProductSnapshot {
            return ProductSnapshot(
                productId = product.id,
                name = product.name,
                categoryId = product.categoryId,
                price = product.price,
                storeId = product.storeId,
                discountRate = product.discountRate,
                discountPrice = product.discountPrice,
                thumbnailUrl = product.thumbnailUrl,
                safeDeliveryFee = product.safeDeliveryFee,
                commonDeliveryFee = product.commonDeliveryFee,
                pickUpDeliveryFee = product.pickUpDeliveryFee,
                productDescriptionId = product.productDescriptionId,
                productInfoId = product.productInfoId,
            )
        }
    }

    fun isProductDetailsMatching(product: Product): Boolean {
        return product.id == productId && product.name == name && product.categoryId == categoryId && product.price == price &&
                product.storeId == storeId && product.discountRate == discountRate && product.discountPrice == discountPrice &&
                product.thumbnailUrl == thumbnailUrl && product.safeDeliveryFee == safeDeliveryFee && product.commonDeliveryFee == commonDeliveryFee &&
                product.pickUpDeliveryFee == pickUpDeliveryFee && product.productDescriptionId == productDescriptionId && product.productInfoId == productInfoId
    }
}
