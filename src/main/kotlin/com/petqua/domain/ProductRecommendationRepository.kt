package com.petqua.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRecommendationRepository : JpaRepository<ProductRecommendation, Long> {
}
