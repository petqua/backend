package com.petqua.domain.product.detail

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class OptimalTankSizeLiter(
    @Column(nullable = false)
    val optimalTankSizeLiterMin: Int,

    @Column(nullable = false)
    val optimalTankSizeLiterMax: Int,
)
