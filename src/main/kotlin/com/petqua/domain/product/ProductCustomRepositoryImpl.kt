package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.common.util.createCountQuery
import com.petqua.common.util.createQuery
import com.petqua.common.util.createSingleQueryOrThrow
import com.petqua.domain.keyword.ProductKeyword
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.detail.description.ProductDescription
import com.petqua.domain.product.detail.description.ProductDescriptionContent
import com.petqua.domain.product.detail.description.ProductDescriptionTitle
import com.petqua.domain.product.detail.info.ProductInfo
import com.petqua.domain.product.dto.ProductDescriptionResponse
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.product.dto.ProductSearchCondition
import com.petqua.domain.product.dto.ProductWithInfoResponse
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.store.Store
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

private const val ESCAPE_LETTER = '\\'
private const val EMPTY_VALUE = ""

@Repository
class ProductCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductCustomRepository {

    override fun findProductWithInfoByIdOrThrow(
        id: Long,
        exceptionSupplier: () -> RuntimeException
    ): ProductWithInfoResponse {
        val query = jpql(ProductDynamicJpqlGenerator) {
            selectNew<ProductWithInfoResponse>(
                entity(Product::class),
                path(Store::name),
                new(
                    ProductDescriptionResponse::class,
                    coalesce(path(ProductDescription::title)(ProductDescriptionTitle::value), EMPTY_VALUE),
                    coalesce(path(ProductDescription::content)(ProductDescriptionContent::value), EMPTY_VALUE)
                ),
                entity(ProductInfo::class),
                entity(Category::class),
                entity(ProductOption::class),
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
                leftJoin(ProductDescription::class).on(path(Product::productDescriptionId).eq(path(ProductDescription::id))),
                join(ProductInfo::class).on(path(Product::productInfoId).eq(path(ProductInfo::id))),
                join(Category::class).on(path(Product::categoryId).eq(path(Category::id))),
                join(ProductOption::class).on(path(Product::id).eq(path(ProductOption::productId)))
            ).whereAnd(
                path(Product::id).eq(id),
                active(),
            )
        }

        return entityManager.createSingleQueryOrThrow<ProductWithInfoResponse>(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            exceptionSupplier
        )
    }

    override fun findAllByCondition(
        condition: ProductReadCondition,
        paging: CursorBasedPaging
    ): List<ProductResponse> {
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
                active(),
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

    override fun countByReadCondition(condition: ProductReadCondition): Int {
        val query = jpql(ProductDynamicJpqlGenerator) {
            select(
                count(Product::id),
            ).from(
                entity(Product::class),
                joinBySourceType(condition.sourceType),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                active(),
            )
        }

        return entityManager.createCountQuery<Int>(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }

    override fun findBySearch(
        condition: ProductSearchCondition,
        paging: CursorBasedPaging
    ): List<ProductResponse> {
        val query = jpql(ProductDynamicJpqlGenerator) {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                path(Product::name).like(pattern = "%${condition.word}%", escape = ESCAPE_LETTER),
                productDeliveryOptionBy(condition.deliveryMethod),
                productIdLt(paging.lastViewedId),
                active(),
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

    override fun countBySearchCondition(condition: ProductSearchCondition): Int {
        val query = jpql(ProductDynamicJpqlGenerator) {
            select(
                count(Product::id),
            ).from(
                entity(Product::class),
            ).whereAnd(
                path(Product::name).like(pattern = "%${condition.word}%", escape = ESCAPE_LETTER),
                productDeliveryOptionBy(condition.deliveryMethod),
                active(),
            )
        }

        return entityManager.createCountQuery<Int>(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }

    override fun findByKeywordSearch(
        condition: ProductSearchCondition,
        paging: CursorBasedPaging
    ): List<ProductResponse> {
        val query = jpql(ProductDynamicJpqlGenerator) {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(ProductKeyword::class).on(path(ProductKeyword::productId).eq(path(Product::id))),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
            ).whereAnd(
                path(ProductKeyword::word).eq(condition.word),
                productDeliveryOptionBy(condition.deliveryMethod),
                productIdLt(paging.lastViewedId),
                active(),
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

    override fun countByKeywordSearchCondition(condition: ProductSearchCondition): Int {
        val query = jpql(ProductDynamicJpqlGenerator) {
            select(
                count(Product::id),
            ).from(
                entity(Product::class),
                join(ProductKeyword::class).on(path(ProductKeyword::productId).eq(path(Product::id))),
            ).whereAnd(
                path(ProductKeyword::word).like(pattern = condition.word, escape = ESCAPE_LETTER),
                productDeliveryOptionBy(condition.deliveryMethod),
                active(),
            )
        }

        return entityManager.createCountQuery<Int>(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }

    override fun findAllProductResponseByIdIn(ids: List<Long>): List<ProductResponse> {
        val query = jpql(ProductDynamicJpqlGenerator) {
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
}
