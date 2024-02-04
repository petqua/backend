package com.petqua.presentation.product

import com.petqua.application.product.ProductService
import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.presentation.product.dto.ProductKeywordRequest
import com.petqua.presentation.product.dto.ProductReadRequest
import com.petqua.presentation.product.dto.ProductSearchRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products")
@RestController
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/{productId}")
    fun readById(
        @PathVariable productId: Long
    ): ResponseEntity<ProductDetailResponse> {
        val response = productService.readById(productId)
        return ResponseEntity.ok(response)
    }

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
