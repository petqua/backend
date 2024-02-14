package com.petqua.domain.product.category

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Family(
    @Column(nullable = false)
    val name: String,
)
