package com.petqua.presentation.product

import com.petqua.application.product.ProductService
import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.presentation.product.dto.ProductKeywordRequest
import com.petqua.presentation.product.dto.ProductReadRequest
import com.petqua.presentation.product.dto.ProductSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Product", description = "상품 관련 API 명세")
@RequestMapping("/products")
@RestController
class ProductController(
    private val productService: ProductService
) {

    @Operation(summary = "상품 상세 조회 API", description = "상품의 상세 정보를 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 상세 조회 성공")
    @GetMapping("/{productId}")
    fun readById(
        @PathVariable productId: Long
    ): ResponseEntity<ProductDetailResponse> {
        val response = productService.readById(productId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "상품 조건 조회 API", description = "상품을 조건에 따라 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 조건 조회 성공")
    @GetMapping
    fun readAll(
        request: ProductReadRequest
    ): ResponseEntity<ProductsResponse> {
        val response = productService.readAll(request.toQuery())
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    fun readBySearch(
        request: ProductSearchRequest
    ): ResponseEntity<ProductsResponse> {
        val response = productService.readBySearch(request.toQuery())
        return ResponseEntity.ok(response)
    }

    @GetMapping("/keywords")
    fun readAutoCompleteKeywords(
        request: ProductKeywordRequest
    ): ResponseEntity<List<ProductKeywordResponse>> {
        val response = productService.readAutoCompleteKeywords(request.toQuery())
        return ResponseEntity.ok(response)
    }
}
