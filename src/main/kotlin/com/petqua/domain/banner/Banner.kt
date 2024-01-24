package com.petqua.domain.banner

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class Banner(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val linkUrl: String,
) : BaseEntity()
