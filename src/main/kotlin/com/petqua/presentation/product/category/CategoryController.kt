package com.petqua.presentation.product.category

import com.petqua.application.product.category.CategoryService
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.product.category.SpeciesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Category", description = "카테고리 관련 API 명세")
@RequestMapping("/categories")
@RestController
class CategoryController(
    private val categoryService: CategoryService
) {

    @Operation(summary = "카테고리 어종 조회 API", description = "어과 정보로 어종 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "카테고리 어종 정보 조회 성공")
    @GetMapping
    fun readSpeciesBy(
        request: CategoryReadRequest,
    ): ResponseEntity<List<SpeciesResponse>> {
        val query = request.toQuery()
        val response = categoryService.readSpecies(query)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "카테고리 상품 조회 API", description = "어과에 속하는 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "카테고리 조회 성공")
    @GetMapping("/products")
    fun readProductsBy(
        request: CategoryProductReadRequest,
    ): ResponseEntity<ProductsResponse> {
        val query = request.toQuery()
        val response = categoryService.readProducts(query)
        return ResponseEntity.ok(response)
    }
}
