package com.petqua.application.product.category

import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.domain.product.dto.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val wishProductRepository: WishProductRepository,
) {

    @Transactional(readOnly = true)
    fun readSpecies(query: CategoryReadQuery): List<SpeciesResponse> {
        val family = query.toFamily()
        return categoryRepository.findSpeciesByFamily(family)
    }

    @Transactional(readOnly = true)
    fun readProducts(query: CategoryProductReadQuery): ProductsResponse {
        val products = categoryRepository.findProductsByCategoryCondition(query.toCondition(), query.toPaging())
        val markedProducts = markWishedProductOf(query.loginMemberOrGuest, products)
        val totalProductsCount = categoryRepository.countProductsByCategoryCondition(query.toCondition())
        return ProductsResponse.of(markedProducts, query.limit, totalProductsCount)
    }

    private fun markWishedProductOf(
        loginMemberOrGuest: LoginMemberOrGuest,
        products: List<ProductResponse>
    ): List<ProductResponse> {
        if (loginMemberOrGuest.isMember()) {
            val wishedProductsIds = wishProductRepository.findWishedProductIdByMemberIdAndProductIdIn(
                memberId = loginMemberOrGuest.memberId,
                productIds = products.map { it.id }
            )
            return products.map { it.copy(isWished = wishedProductsIds.contains(it.id)) }
        }
        return products
    }
}
