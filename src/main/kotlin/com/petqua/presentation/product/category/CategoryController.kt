package com.petqua.presentat어ion.product.category

import com.petqua.application.product.category.CategoryService
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.presentation.product.category.CategoryProductReadRequest
import com.petqua.presentation.product.category.CategoryReadRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/categories")
@RestController
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun readSpeciesBy(
        request: CategoryReadRequest,
    ): ResponseEntity<List<SpeciesResponse>> {
        val query = request.toQuery()
        val response = categoryService.readSpecies(query)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/products")
    fun readProductsBy(
        request: CategoryProductReadRequest,
    ): ResponseEntity<ProductsResponse> {
        val query = request.toQuery()
        val response = categoryService.readProducts(query)
        return ResponseEntity.ok(response)
    }
}
