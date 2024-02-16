package com.petqua.domain.product.review

import com.petqua.common.domain.BaseEntity
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
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

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "score"))
    val score: ProductReviewScore,

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
