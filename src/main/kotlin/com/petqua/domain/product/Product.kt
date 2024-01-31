package com.petqua.domain.product

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
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
    val category: String,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = false)
    val storeId: Long = 0,

    @Column(nullable = false)
    val discountRate: Int = 0,

    @Column(nullable = false)
    val discountPrice: BigDecimal = price,

    @Column(nullable = false)
    var wishCount: Int = 0,

    @Column(nullable = false)
    val reviewCount: Int = 0,

    @Column(nullable = false)
    val reviewTotalScore: Int = 0,

    @Column(nullable = false)
    val thumbnailUrl: String,

    @Column(nullable = false)
    val description: String,
) : BaseEntity() {

    fun averageReviewScore(): Double {
        return if (reviewCount == ZERO) ZERO.toDouble()
        else BigDecimal.valueOf(reviewTotalScore / reviewCount.toDouble())
            .setScale(SCALE, RoundingMode.HALF_UP)
            .toDouble()
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name', category='$category', price=$price, storeId=$storeId, discountRate=$discountRate, discountPrice=$discountPrice, wishCount=$wishCount, reviewCount=$reviewCount, reviewTotalScore=$reviewTotalScore, thumbnailUrl='$thumbnailUrl', description='$description')"
    }
}
