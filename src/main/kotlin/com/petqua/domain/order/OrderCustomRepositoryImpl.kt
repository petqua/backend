package com.petqua.domain.order

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository


@Repository
class OrderCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : OrderCustomRepository {

    override fun findOrdersByMemberId(
        memberId: Long,
        orderPaging: OrderPaging,
    ): List<Order> {
        val latestOrderNumbers = findLatestOrderNumbers(memberId, orderPaging)
        val query = jpql {
            select(
                entity(Order::class),
            ).from(
                entity(Order::class),
            ).whereAnd(
                path(Order::orderNumber)(OrderNumber::value).`in`(latestOrderNumbers),
            ).orderBy(
                path(Order::createdAt).desc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
        )
    }

    private fun findLatestOrderNumbers(
        memberId: Long,
        paging: OrderPaging,
    ): List<String> {
        val query = jpql(OrderDynamicJpqlGenerator) {
            selectDistinct(
                path(Order::orderNumber)(OrderNumber::value)
            ).from(
                entity(Order::class)
            ).whereAnd(
                path(Order::memberId).eq(memberId),
                orderIdLt(paging.lastViewedId),
                orderNumberNotEq(paging.lastViewedOrderNumber),
            ).orderBy(
                path(Order::createdAt).desc()
            )
        }

        return entityManager.createQuery<String>(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            paging.limit,
        )
    }
}
