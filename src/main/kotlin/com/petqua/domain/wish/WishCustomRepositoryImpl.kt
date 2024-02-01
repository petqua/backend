package com.petqua.domain.wish

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import com.petqua.domain.product.Product
import com.petqua.domain.store.Store
import com.petqua.presentation.wish.WishResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class WishCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : WishCustomRepository {
    override fun readAllWishResponse(memberId: Long): List<WishResponse> {
        val query = jpql {
            selectNew<WishResponse>(
                path(Wish::id),
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Wish::class),
                join(Product::class).on(path(Wish::productId).eq(path(Product::id))),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).where(
                path(Wish::memberId).eq(memberId)
            ).orderBy(
                path(Wish::id).desc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }
}
