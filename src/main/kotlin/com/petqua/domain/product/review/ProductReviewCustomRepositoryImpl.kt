package com.petqua.domain.product.review

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.util.createQuery
import com.petqua.domain.member.Member
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ProductReviewCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductReviewCustomRepository {

    override fun findAllByCondition(
        condition: ProductReviewReadCondition,
        paging: CursorBasedPaging
    ): List<ProductReviewWithMemberResponse> {

        val query = jpql(ProductReviewDynamicJpqlGenerator) {
            selectDistinctNew<ProductReviewWithMemberResponse>(
                entity(ProductReview::class),
                entity(Member::class),
            ).from(
                entity(ProductReview::class),
                join(Member::class).on(path(ProductReview::memberId).eq(path(Member::id))),
            ).whereAnd(
                path(ProductReview::productId).eq(condition.productId),
                photoOnlyEq(condition.photoOnly),
                productReviewScoreEq(condition.score),
                productReviewIdLt(paging.lastViewedId),
            ).orderBy(
                sortBy(condition.sorter),
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
