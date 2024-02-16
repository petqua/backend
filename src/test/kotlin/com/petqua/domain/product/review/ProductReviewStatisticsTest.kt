package com.petqua.domain.product.review

import com.petqua.domain.product.dto.ProductReviewScoreWithCount
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ProductReviewStatisticsTest : StringSpec({
    "상품 후기 통계 생성" {
        val reviewScoreWithCounts = listOf(
            ProductReviewScoreWithCount(5, 3),
            ProductReviewScoreWithCount(4, 2),
            ProductReviewScoreWithCount(2, 1),
            ProductReviewScoreWithCount(1, 1),
        )

        val statistics = ProductReviewStatistics.from(reviewScoreWithCounts)

        assertSoftly(statistics) {
            totalReviewCount shouldBe 7
            averageScore shouldBe 3.7
            productSatisfaction shouldBe 71
            scoreFiveCount shouldBe 3
            scoreFourCount shouldBe 2
            scoreThreeCount shouldBe 0
            scoreTwoCount shouldBe 1
            scoreOneCount shouldBe 1
        }
    }
})
