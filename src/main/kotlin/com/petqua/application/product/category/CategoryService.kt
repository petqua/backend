package com.petqua.application.product.category

import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {

    @Transactional(readOnly = true)
    fun readSpecies(query: CategoryReadQuery): List<SpeciesResponse> {
        val family = query.toFamily()
        return categoryRepository.findSpeciesByFamily(family)
    }

    @Transactional(readOnly = true)
    fun readProducts(query: CategoryProductReadQuery): ProductsResponse {
        val products = categoryRepository.findProductsByCondition(query.toCondition(), query.toPaging())
        return ProductsResponse.of(products, query.limit, 1)
    }
}
