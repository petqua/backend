package com.petqua.domain.product.category

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Category(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Embedded
    @AttributeOverride(name = "name", column = Column(name = "family", nullable = false))
    val family: Family,

    @Embedded
    @AttributeOverride(name = "name", column = Column(name = "species", nullable = false))
    val species: Species,
)
