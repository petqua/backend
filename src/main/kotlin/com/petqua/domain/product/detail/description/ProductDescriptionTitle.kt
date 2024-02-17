package com.petqua.domain.product.detail.description

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ProductDescriptionTitle(
    @Column(nullable = false)
    val title: String,
)
