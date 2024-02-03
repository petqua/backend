package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createCountQuery
import com.petqua.common.util.createQuery
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
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
        val query = jpql(ProductDynamicJpqlGenerator) {
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

    // cache 추가하면 어떨까요?
    override fun countByCondition(condition: ProductReadCondition): Int {
        val query = jpql(ProductDynamicJpqlGenerator) {
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

    override fun findBySearch(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse> {
        val query = jpql(ProductDynamicJpqlGenerator) {
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

    override fun findByKeywordSearch(condition: ProductReadCondition, paging: ProductPaging): List<ProductResponse> {
        val query = jpql(ProductDynamicJpqlGenerator) {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(ProductKeyword::class).on(path(ProductKeyword::productId).eq(path(Product::id))),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                productIdLt(paging.lastViewedId),
                path(ProductKeyword::word).eq(condition.word)
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

    override fun countByKeywordCondition(condition: ProductReadCondition): Int {
        val query = jpql(ProductDynamicJpqlGenerator) {
            select(
                count(Product::id),
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
                join(ProductKeyword::class).on(path(ProductKeyword::productId).eq(path(Product::id))),
            ).whereAnd(
                path(ProductKeyword::word).like(pattern = condition.word, escape = ESCAPE_LETTER)
            )
        }

        return entityManager.createCountQuery<Int>(
            query,
            jpqlRenderContext,
            jpqlRenderer
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
