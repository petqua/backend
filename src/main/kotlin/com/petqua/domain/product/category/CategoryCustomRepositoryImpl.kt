package com.petqua.domain.product.category

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import com.petqua.domain.product.Product
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.Store
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class CategoryCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : CategoryCustomRepository {

    override fun findProductsByCondition(
        condition: CategoryProductReadCondition,
        paging: CategoryProductPaging
    ): List<ProductResponse> {
        val query = jpql(CategoryDynamicJpqlGenerator) {
            selectNew<ProductResponse>(
                entity(Product::class),
                path(Store::name)
            ).from(
                entity(Product::class),
                join(Store::class).on(path(Product::storeId).eq(path(Store::id))),
                join(Category::class).on(path(Product::categoryId).eq(path(Category::id)))
            ).whereAnd(
                categoryFamilyEq(condition.family),
                categorySpeciesEqOr(condition.species),

                productDeliveryOptionBy(
                    canDeliverSafely = condition.canDeliverSafely,
                    canDeliverCommonly = condition.canDeliverCommonly,
                    canPickUp = condition.canPickUp,
                ),

                productIdLt(paging.lastViewedId),
                active(),
            ).orderBy(
                sortBy(condition.sorter),
                sortBy(ENROLLMENT_DATE_DESC)
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
