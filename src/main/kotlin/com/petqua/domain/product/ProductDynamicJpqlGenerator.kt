package com.petqua.domain.product

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.join.JoinAsStep
import com.linecorp.kotlinjdsl.dsl.jpql.sort.SortNullsStep
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicatable
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.category.Category
import com.petqua.domain.product.category.Family
import com.petqua.domain.product.category.Species
import com.petqua.domain.recommendation.ProductRecommendation

class ProductDynamicJpqlGenerator : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<ProductDynamicJpqlGenerator> {
        override fun newInstance(): ProductDynamicJpqlGenerator = ProductDynamicJpqlGenerator()
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

    fun Jpql.joinBySourceType(sourceType: ProductSourceType): JoinAsStep<ProductRecommendation>? {
        return when (sourceType) {
            ProductSourceType.HOME_RECOMMENDED -> join(ProductRecommendation::class)
                .on(path(Product::id).eq(path(ProductRecommendation::productId)))

            else -> null
        }
    }

    fun Jpql.predicateByIds(ids: List<Long>): Predicate? {
        return if (ids.isEmpty()) null else path(Product::id).`in`(ids)
    }

    fun Jpql.productDeliveryOptionBy(deliveryMethod: DeliveryMethod): Predicate? {
        return when (deliveryMethod) {
            DeliveryMethod.SAFETY -> path(Product::canDeliverSafely).eq(true)
            DeliveryMethod.COMMON -> path(Product::canDeliverCommonly).eq(true)
            DeliveryMethod.PICK_UP -> path(Product::canPickUp).eq(true)
            else -> null
        }
    }

    fun Jpql.active(): Predicate {
        return path(Product::isDeleted).eq(false)
    }

    fun Jpql.categoryFamilyEq(family: String?): Predicate? {
        return family?.let { path(Category::family)(Family::name).eq(it) }
    }

    fun Jpql.categorySpeciesEqOr(species: List<String>): Predicate? {
        return if (species.isNotEmpty()) or(species.map { path(Category::species)(Species::name).eq(it) }) else null
    }

    private fun or(predicates: List<Predicatable>): Predicate {
        return or(*predicates.toTypedArray())
    }
}

