package com.petqua.domain.product.detail.description

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ProductDescriptionContent(
    @Column(nullable = false, name = "content")
    val value: String,
)
