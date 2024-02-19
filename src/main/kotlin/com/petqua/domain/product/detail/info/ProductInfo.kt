package com.petqua.domain.product.detail.info

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class ProductInfo(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val categoryId: Long,

    @Embedded
    val optimalTemperature: OptimalTemperature,

    @Column(nullable = false)
    @Enumerated(STRING)
    val difficultyLevel: DifficultyLevel,

    @Column(nullable = false)
    @Enumerated(STRING)
    val optimalTankSize: OptimalTankSize,

    @Column(nullable = false)
    @Enumerated(STRING)
    val temperament: Temperament,
) : BaseEntity() {
}
