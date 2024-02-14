package com.petqua.domain.product.review

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
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

    @Column(nullable = false)
    var recommendCount: Int = 0,

    @Column(nullable = false)
    val hasPhotos: Boolean = false,
) : BaseEntity() {

    fun increaseRecommendCount() {
        recommendCount += 1
    }

    fun decreaseRecommendCount() {
        recommendCount -= 1
    }
}
