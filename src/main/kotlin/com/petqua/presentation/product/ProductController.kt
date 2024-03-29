package com.petqua.presentation.product

import com.petqua.application.product.ProductService
import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.presentation.product.dto.ProductKeywordRequest
import com.petqua.presentation.product.dto.ProductReadRequest
import com.petqua.presentation.product.dto.ProductSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @GetMapping("/{productId}")
    fun readById(
        @Auth loginMemberOrGuest: LoginMemberOrGuest,
        @PathVariable productId: Long
    ): ResponseEntity<ProductDetailResponse> {
        val response = productService.readById(loginMemberOrGuest, productId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "상품 조건 조회 API", description = "상품을 조건에 따라 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 조건 조회 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @GetMapping
    fun readAll(
        @Auth loginMemberOrGuest: LoginMemberOrGuest,
        request: ProductReadRequest
    ): ResponseEntity<ProductsResponse> {
        val response = productService.readAll(request.toQuery(loginMemberOrGuest))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "상품 검색 API", description = "검색으로 상품을 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 검색 조회 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @GetMapping("/search")
    fun readBySearch(
        @Auth loginMemberOrGuest: LoginMemberOrGuest,
        request: ProductSearchRequest
    ): ResponseEntity<ProductsResponse> {
        val response = productService.readBySearch(request.toQuery(loginMemberOrGuest))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "상품 추천 검색어(자동완성) 조회 API", description = "추천 검색어를 조회합니다")
    @ApiResponse(responseCode = "200", description = "추천 검색어 조회 성공")
    @GetMapping("/keywords")
    fun readAutoCompleteKeywords(
        request: ProductKeywordRequest
    ): ResponseEntity<List<ProductKeywordResponse>> {
        val response = productService.readAutoCompleteKeywords(request.toQuery())
        return ResponseEntity.ok(response)
    }
}
