package com.petqua.domain.product.detail.description

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id

@Entity
class ProductDescription(
    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long,

    @Column(nullable = false)
    val productId: Long,

    @Embedded
    val productDescriptionTitle: ProductDescriptionTitle,

    @Embedded
    val productDescriptionContent: ProductDescriptionContent,
) : BaseEntity() {
}
