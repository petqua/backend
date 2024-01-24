package com.petqua.domain

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.application.ProductReadConditions
import com.petqua.application.Sorter
import com.petqua.application.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.application.Sorter.REVIEW_COUNT_DESC
import com.petqua.application.Sorter.SALE_PRICE_ASC
import com.petqua.application.Sorter.SALE_PRICE_DESC
import com.petqua.common.util.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ProductCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductCustomRepository {

    override fun findAllByConditions(conditions: ProductReadConditions): List<Product> {
        val query = jpql {
            select(
                entity(Product::class)
            ).from(
                entity(Product::class)
            ).whereAnd(
                productIdLt(conditions.lastViewedId)
            ).orderBy(
                pathSortBy(conditions.sorter)
            )
        }

        return entityManager.createQuery(query, jpqlRenderContext, jpqlRenderer, conditions.limit)
    }

    private fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Product::id).lt(it) }
    }

    private fun Jpql.pathSortBy(sorter: Sorter): SortNullsStep? {
        return when (sorter) {
            SALE_PRICE_ASC -> path(Product::discountPrice).asc()
            SALE_PRICE_DESC -> path(Product::discountPrice).desc()
            REVIEW_COUNT_DESC -> path(Product::reviewCount).desc()
            ENROLLMENT_DATE_DESC -> path(Product::id).desc()
            else -> null
        }
    }
}
