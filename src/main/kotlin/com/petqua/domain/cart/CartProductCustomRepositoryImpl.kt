package com.petqua.domain.cart

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.application.cart.dto.CartProductResponse
import com.petqua.common.util.createQuery
import com.petqua.domain.product.Product
import com.petqua.domain.store.Store
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class CartProductCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : CartProductCustomRepository {

    override fun findAllCartResultsByMemberId(memberId: Long): List<CartProductResponse> {
        val query = jpql {
            selectNew<CartProductResponse>(
                entity(CartProduct::class),
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(CartProduct::class),
                leftJoin(Product::class).on(path(CartProduct::productId).eq(path(Product::id))),
                leftJoin(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).where(
                path(CartProduct::memberId).eq(memberId)
            ).orderBy(
                path(CartProduct::createdAt).desc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }
}
