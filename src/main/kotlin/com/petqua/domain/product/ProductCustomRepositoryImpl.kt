package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.join.JoinAsStep
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createCountQuery
import com.petqua.common.util.createQuery
import com.petqua.domain.product.ProductSourceType.HOME_RECOMMENDED
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.Sorter.NONE
import com.petqua.domain.product.Sorter.REVIEW_COUNT_DESC
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.Sorter.SALE_PRICE_DESC
import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.recommendation.ProductRecommendation
import com.petqua.domain.store.Store
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

private const val ESCAPE_LETTER = '\\'

@Repository
class ProductCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductCustomRepository {

    override fun findAllByCondition(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse> {
        val query = jpql {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                joinBySourceType(condition.sourceType),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                productIdLt(paging.lastViewedId),
            ).orderBy(
                sortBy(condition.sorter),
                sortBy(ENROLLMENT_DATE_DESC),
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            paging.limit
        )
    }

    private fun Jpql.joinBySourceType(sourceType: ProductSourceType): JoinAsStep<ProductRecommendation>? {
        return when (sourceType) {
            HOME_RECOMMENDED -> join(ProductRecommendation::class)
                .on(path(Product::id).eq(path(ProductRecommendation::productId)))

            else -> null
        }
    }

    private fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Product::id).lt(it) }
    }

    private fun Jpql.sortBy(sorter: Sorter): SortNullsStep? {
        return when (sorter) {
            SALE_PRICE_ASC -> path(Product::discountPrice).asc()
            SALE_PRICE_DESC -> path(Product::discountPrice).desc()
            REVIEW_COUNT_DESC -> path(Product::reviewCount).desc()
            ENROLLMENT_DATE_DESC -> path(Product::id).desc()
            NONE -> null
        }
    }

    // cache 추가하면 어떨까요?
    override fun countByCondition(condition: ProductReadCondition): Int {
        val query = jpql {
            select(
                count(Product::id),
            ).from(
                entity(Product::class),
                joinBySourceType(condition.sourceType),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                productNameLike(condition.word)
            )
        }

        return entityManager.createCountQuery<Int>(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }

    private fun Jpql.productNameLike(word: String): Predicate? {
        return if (word.isBlank()) null else path(Product::name).like(pattern = "%$word%", escape = ESCAPE_LETTER)
    }

    override fun findBySearch(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse> {
        val query = jpql {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                productIdLt(paging.lastViewedId),
                path(Product::name).like(pattern = "%${condition.word}%", escape = ESCAPE_LETTER)
            ).orderBy(
                sortBy(ENROLLMENT_DATE_DESC),
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            paging.limit
        )
    }

    override fun findAllProductResponseByIdIn(ids: List<Long>): List<ProductResponse> {
        val query = jpql {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).where(
                predicateByIds(ids)
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }

    private fun Jpql.predicateByIds(ids: List<Long>) = if (ids.isEmpty()) null else path(Product::id).`in`(ids)
}
