package com.petqua.domain.product.review

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import com.petqua.domain.member.Member
import com.petqua.domain.product.dto.ProductReviewPaging
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
        paging: ProductReviewPaging
    ): List<ProductReviewWithMemberResponse> {

        val query = jpql {
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

    fun Jpql.productReviewIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(ProductReview::id).lt(it) }
    }

    fun Jpql.photoOnlyEq(photoOnly: Boolean): Predicate? {
        return if (photoOnly) path(ProductReview::hasPhotos).eq(true) else null
    }

    fun Jpql.productReviewScoreEq(score: Int?): Predicate? {
        return score?.let { path(ProductReview::score).eq(it) }
    }

    fun Jpql.sortBy(sorter: ProductReviewSorter): SortNullsStep? {
        return when (sorter) {
            ProductReviewSorter.REVIEW_DATE_DESC -> path(ProductReview::createdAt).desc()
            ProductReviewSorter.RECOMMEND_DESC -> path(ProductReview::recommendCount).desc()
        }
    }
}
