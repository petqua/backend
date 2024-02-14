package com.petqua.domain.product.review

import com.petqua.domain.product.dto.ProductReviewScoreWithCount
import java.math.BigDecimal
import java.math.RoundingMode

private const val ZERO = 0

class ProductReviewStatistics private constructor(
    private val reviewCountsByScore: Map<Int, Int>,
) {

    companion object {
        fun from(reviewScoreWithCounts: List<ProductReviewScoreWithCount>): ProductReviewStatistics {
            val reviewCountsByScore = reviewScoreWithCounts.associate { it.score to it.count.toInt() }
            return ProductReviewStatistics(reviewCountsByScore)
        }

        fun averageReviewScore(reviewTotalScore: Int, totalReviewCount: Double): Double {
            return if (totalReviewCount == 0.0) 0.0
            else BigDecimal.valueOf(reviewTotalScore / totalReviewCount)
                .setScale(1, RoundingMode.HALF_UP)
                .toDouble()
        }
    }

    val totalReviewCount: Int
        get() = reviewCountsByScore.values.sum()


    val averageScore: Double
        get() = averageReviewScore(
            reviewCountsByScore.entries.sumOf { it.key * it.value },
            totalReviewCount.toDouble()
        )

    val productSatisfaction: Int
        get() = if (totalReviewCount == ZERO) ZERO
        else BigDecimal.valueOf((scoreFiveCount + scoreFourCount) / totalReviewCount.toDouble() * 100)
            .setScale(0, RoundingMode.HALF_UP)
            .toInt()

    val scoreFiveCount: Int
        get() = reviewCountsByScore[5] ?: 0

    val scoreFourCount: Int
        get() = reviewCountsByScore[4] ?: 0

    val scoreThreeCount: Int
        get() = reviewCountsByScore[3] ?: 0

    val scoreTwoCount: Int
        get() = reviewCountsByScore[2] ?: 0

    val scoreOneCount: Int
        get() = reviewCountsByScore[1] ?: 0
}
