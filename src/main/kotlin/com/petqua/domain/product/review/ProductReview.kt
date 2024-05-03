package com.petqua.domain.product.review

import com.petqua.common.domain.BaseEntity
import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.web.multipart.MultipartFile

@Entity
class ProductReview(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Embedded
    @AttributeOverride(name = "value", column = Column(name = "content"))
    val content: ProductReviewContent,

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

    companion object {
        private const val MAX_REVIEW_IMAGES_COUNT = 10

        fun of(
            memberId: Long,
            productId: Long,
            content: String,
            score: Int,
            images: List<MultipartFile>,
        ): ProductReview {
            throwExceptionWhen(images.size > MAX_REVIEW_IMAGES_COUNT) {
                ProductReviewException(EXCEEDED_REVIEW_IMAGES_COUNT_LIMIT)
            }
            return ProductReview(
                memberId = memberId,
                productId = productId,
                content = ProductReviewContent(content),
                score = ProductReviewScore(score),
                hasPhotos = images.isNotEmpty()
            )
        }
    }
}
