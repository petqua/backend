package com.petqua.domain.product.review

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.petqua.domain.product.review.ProductReviewSorter.RECOMMEND_DESC
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC

class ProductReviewDynamicJpqlGenerator : Jpql() {

    companion object Constructor : JpqlDsl.Constructor<ProductReviewDynamicJpqlGenerator> {
        override fun newInstance(): ProductReviewDynamicJpqlGenerator = ProductReviewDynamicJpqlGenerator()
    }

    fun Jpql.productReviewIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(ProductReview::id).lt(it) }
    }

    fun Jpql.photoOnlyEq(photoOnly: Boolean): Predicate? {
        return if (photoOnly) path(ProductReview::hasPhotos).eq(true) else null
    }

    fun Jpql.productReviewScoreEq(score: Int?): Predicate? {
        return score?.let { path(ProductReview::score).eq(it) }
    }

    fun Jpql.sortBy(sorter: ProductReviewSorter): SortNullsStep {
        return when (sorter) {
            REVIEW_DATE_DESC -> path(ProductReview::createdAt).desc()
            RECOMMEND_DESC -> path(ProductReview::recommendCount).desc()
        }
    }
}
