package com.petqua.domain.product.review

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

class ProductReviewImages(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val imageUrl: String,

    @Column(nullable = false)
    val productReviewId: Long,
) : BaseEntity() {
}
