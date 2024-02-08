package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.store.Store
import com.petqua.presentation.product.dto.WishProductResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class WishProductCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : WishProductCustomRepository {
    override fun readAllWishProductResponse(memberId: Long, paging: ProductPaging): List<WishProductResponse> {
        val query = jpql {
            selectNew<WishProductResponse>(
                path(WishProduct::id),
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(WishProduct::class),
                join(Product::class).on(path(WishProduct::productId).eq(path(Product::id))),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                path(WishProduct::memberId).eq(memberId),
                productIdLt(paging.lastViewedId),
            ).orderBy(
                path(WishProduct::id).desc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            paging.limit
        )
    }

    private fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(WishProduct::id).lt(it) }
    }
}
