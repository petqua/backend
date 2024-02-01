package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.join.JoinAsStep
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.petqua.domain.recommendation.ProductRecommendation

private const val ESCAPE_LETTER = '\\'

class ProductJpql : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<ProductJpql> {
        override fun newInstance(): ProductJpql = ProductJpql()
    }

    fun Jpql.sortBy(sorter: Sorter): SortNullsStep? {
        return when (sorter) {
            Sorter.SALE_PRICE_ASC -> path(Product::discountPrice).asc()
            Sorter.SALE_PRICE_DESC -> path(Product::discountPrice).desc()
            Sorter.REVIEW_COUNT_DESC -> path(Product::reviewCount).desc()
            Sorter.ENROLLMENT_DATE_DESC -> path(Product::id).desc()
            Sorter.NONE -> null
        }
    }

    fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Product::id).lt(it) }
    }

    fun Jpql.joinBySourceType(sourceType: ProductSourceType): JoinAsStep<ProductRecommendation>? {
        return when (sourceType) {
            ProductSourceType.HOME_RECOMMENDED -> join(ProductRecommendation::class)
                .on(path(Product::id).eq(path(ProductRecommendation::productId)))

            else -> null
        }
    }

    fun Jpql.productNameLike(word: String): Predicate? {
        return if (word.isBlank()) null else path(Product::name).like(pattern = "%$word%", escape = ESCAPE_LETTER)
    }
}
