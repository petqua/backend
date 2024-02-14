package com.petqua.domain.product.review

import org.springframework.data.jpa.repository.JpaRepository

interface ProductReviewRepository : JpaRepository<ProductReview, Long>, ProductReviewCustomRepository {
}
