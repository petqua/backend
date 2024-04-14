package com.petqua.domain.order

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createFirstQueryOrThrow
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : OrderPaymentCustomRepository {

    override fun findLatestByOrderIdOrThrow(
        orderId: Long,
        exceptionSupplier: () -> RuntimeException,
    ): OrderPayment {
        val query = jpql {
            select(
                entity(OrderPayment::class),
            ).from(
                entity(OrderPayment::class),
            ).whereAnd(
                path(OrderPayment::orderId).eq(orderId)
            ).orderBy(
                path(OrderPayment::id).desc()
            )
        }

        return entityManager.createFirstQueryOrThrow(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            exceptionSupplier
        )
    }
}
