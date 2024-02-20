package com.petqua.domain.product.detail.description

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class ProductDescription(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0,

    @Embedded
    val title: ProductDescriptionTitle,

    @Embedded
    val content: ProductDescriptionContent,
) : BaseEntity() {
}
