package com.petqua.domain.product.review

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

class ProductReview(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val memberId: Long,

    @Column(nullable = false)
    val score: Int,
) : BaseEntity() {
}
