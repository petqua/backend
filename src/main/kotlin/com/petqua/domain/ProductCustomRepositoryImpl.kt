package com.petqua.domain

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.application.ProductReadConditions
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
                    productIdLt(conditions.lastViewedId),
            )
        }

        return entityManager.createQuery(query, jpqlRenderContext, jpqlRenderer, conditions.limit)
    }

    private fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return if (lastViewedId != null) path(Product::id).lt(lastViewedId) else null
    }
}
