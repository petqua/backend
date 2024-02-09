package com.petqua.domain.product.category

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.petqua.common.domain.conditionToPredicate
import com.petqua.domain.product.Product
import com.petqua.domain.product.Sorter

class CategoryDynamicJpqlGenerator : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CategoryDynamicJpqlGenerator> {
        override fun newInstance(): CategoryDynamicJpqlGenerator = CategoryDynamicJpqlGenerator()
    }

    fun Jpql.sortBy(sorter: Sorter): SortNullsStep? {
        return when (sorter) {
            Sorter.SALE_PRICE_ASC -> path(Product::discountPrice).asc()
            Sorter.SALE_PRICE_DESC -> path(Product::discountPrice).desc()
            Sorter.REVIEW_COUNT_DESC -> path(Product::reviewCount).desc()
            Sorter.ENROLLMENT_DATE_DESC -> path(Product::id).desc()
            Sorter.NONE -> null
        }
    }

    fun Jpql.productIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Product::id).lt(it) }
    }

    fun Jpql.categoryFamilyEq(family: String?): Predicate? {
        return family?.let { path(Category::family)(Family::name).eq(it) }
    }

    fun Jpql.categorySpeciesEq(species: String?): Predicate? {
        return species?.let { path(Category::species)(Species::name).eq(it) }
    }

    fun Jpql.productDeliveryOptionBy(
        canDeliverSafely: Boolean?,
        canDeliverCommonly: Boolean?,
        canPickUp: Boolean?
    ): Predicate? {
        return conditionToPredicate(canDeliverSafely) { path(Product::canDeliverSafely).eq(it) }
            ?: conditionToPredicate(canDeliverCommonly) { path(Product::canDeliverCommonly).eq(it) }
            ?: conditionToPredicate(canPickUp) { path(Product::canPickUp).eq(it) }
    }

    fun Jpql.active(): Predicate {
        return path(Product::isDeleted).eq(false)
    }
}
