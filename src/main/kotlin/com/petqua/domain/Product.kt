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
}
