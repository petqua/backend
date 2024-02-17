package com.petqua.domain.product.detail.description

import jakarta.persistence.Embeddable

@Embeddable
data class ProductDescriptionContent(
    val content: String,
)
