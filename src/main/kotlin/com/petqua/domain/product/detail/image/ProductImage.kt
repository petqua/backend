package com.petqua.domain.product.detail.image

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class ProductImage(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    @Enumerated(STRING)
    val imageType: ImageType,
) : BaseEntity()
