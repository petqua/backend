package com.petqua.presentation.product.category

import com.petqua.application.product.category.CategoryService
import com.petqua.domain.product.category.SpeciesResponse
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
    fun readSpecies(
        request: CategoryReadRequest,
    ): ResponseEntity<List<SpeciesResponse>> {
        val query = request.toQuery()
        val response = categoryService.readSpecies(query)
        return ResponseEntity.ok(response)
    }

    // get(/categories/products?family=송사리과)
    // 카테고리 검색

    // get(/categories/products?family=송사리과&species=고정구피)
    // 카테고리 검색 후 어종 선택 검색

    // get(/categories/products?family=송사리과&species=고정구피&canDeliverSafely=true)
    // 카테고리 검색 후 어종 선택 검색 및 운송 방법 설정

    // get(/categories/products?family=송사리과&species=고정구피&canDeliverSafely=true&sorter=)
    // 카테고리 검색 후 어종 선택 검색 및 운송 방법 설정, 정렬
}
