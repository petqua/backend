package com.petqua.domain.product.review

enum class ProductReviewSorter(
    private val description: String,
) {
    REVIEW_DATE_DESC("최신순"),
    RECOMMEND_DESC("추천순"),
}
