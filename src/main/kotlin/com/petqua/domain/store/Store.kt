package com.petqua.domain.store

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class Store(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val name: String,
) : BaseEntity() {
}
