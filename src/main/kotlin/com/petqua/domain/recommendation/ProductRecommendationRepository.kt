package com.petqua.domain.recommendation

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRecommendationRepository : JpaRepository<ProductRecommendation, Long> {
}
