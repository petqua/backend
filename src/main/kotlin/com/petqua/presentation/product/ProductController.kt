package com.petqua.presentation.product

import com.petqua.application.product.ProductService
import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
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
        @Auth loginMember: LoginMember,
        @PathVariable productId: Long
    ): ResponseEntity<ProductDetailResponse> {
        val response = productService.readById(productId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        request: ProductReadRequest
    ): ResponseEntity<ProductsResponse> {
        val command = request.toQuery(loginMember.memberId)
        val response = productService.readAll(command)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    fun readBySearch(
        @Auth loginMember: LoginMember,
        request: ProductSearchRequest
    ): ResponseEntity<ProductsResponse> {
        val command = request.toQuery(loginMember.memberId)
        val response = productService.readBySearch(command)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/keywords")
    fun readKeywords(
        @Auth loginMember: LoginMember,
        request: ProductKeywordRequest
    ): ResponseEntity<List<ProductKeywordResponse>> {
        val command = request.toQuery(loginMember.memberId)
        val response = productService.readKeywords(command)
        return ResponseEntity.ok(response)
    }
}
