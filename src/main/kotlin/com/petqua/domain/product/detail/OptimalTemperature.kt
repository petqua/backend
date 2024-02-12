package com.petqua.domain.product.detail

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class OptimalTemperature(
    @Column(nullable = false)
    val optimalTemperatureMin: Int,

    @Column(nullable = false)
    val optimalTemperatureMax: Int,
)
