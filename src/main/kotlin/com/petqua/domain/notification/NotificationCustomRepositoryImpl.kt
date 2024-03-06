package com.petqua.domain.notification

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.application.notification.dto.NotificationResponse
import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.util.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class NotificationCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : NotificationCustomRepository {

    override fun findAllByMemberId(memberId: Long, paging: CursorBasedPaging): List<NotificationResponse> {
        val query = jpql(NotificationDynamicJpqlGenerator) {
            selectNew<NotificationResponse>(
                entity(Notification::class)
            ).from(
                entity(Notification::class),
            ).whereAnd(
                path(Notification::memberId).eq(memberId),
                notificationIdLt(paging.lastViewedId),
            ).orderBy(
                path(Notification::createdAt).desc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            paging.limit
        )
    }
}
