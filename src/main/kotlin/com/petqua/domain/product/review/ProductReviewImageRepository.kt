package com.petqua.domain.product.review

import org.springframework.data.jpa.repository.JpaRepository

interface ProductReviewImageRepository : JpaRepository<ProductReviewImage, Long> {

    fun findAllByProductReviewIdIn(productReviewId: List<Long>): List<ProductReviewImage>
}
