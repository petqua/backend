package com.petqua.domain

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

private const val SCALE = 1
private const val ZERO = 0

@Entity
class Product(
    @Id @GeneratedValue(strategy = IDENTITY)
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
    val wishCount: Int = 0,

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
            .setScale(SCALE, HALF_UP)
            .toDouble()

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        return when {
            id != other.id -> false
            name != other.name -> false
            category != other.category -> false
            price != other.price -> false
            storeId != other.storeId -> false
            discountRate != other.discountRate -> false
            discountPrice != other.discountPrice -> false
            wishCount != other.wishCount -> false
            reviewCount != other.reviewCount -> false
            reviewTotalScore != other.reviewTotalScore -> false
            thumbnailUrl != other.thumbnailUrl -> false
            else -> description == other.description
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + storeId.hashCode()
        result = 31 * result + discountRate
        result = 31 * result + discountPrice.hashCode()
        result = 31 * result + wishCount
        result = 31 * result + reviewCount
        result = 31 * result + reviewTotalScore
        result = 31 * result + thumbnailUrl.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String {
        return "Product(id=$id, name='$name', category='$category', price=$price, storeId=$storeId, discountRate=$discountRate, discountPrice=$discountPrice, wishCount=$wishCount, reviewCount=$reviewCount, reviewTotalScore=$reviewTotalScore, thumbnailUrl='$thumbnailUrl', description='$description')"
    }


}
