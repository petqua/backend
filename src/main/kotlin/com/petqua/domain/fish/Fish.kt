package com.petqua.domain.fish

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Fish(
    @Id @GeneratedValue
    val id: Long = 0L,

    @Embedded
    val species: Species,
) {
}
