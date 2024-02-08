package com.petqua.domain.product.category

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Species(
    @Column(nullable = false)
    val name: String,
)
