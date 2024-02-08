package com.petqua.presentation.product

import com.petqua.application.product.dto.ProductReviewScoreStatistics
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.review.ProductReviewService
import com.petqua.presentation.product.dto.ReadAllProductReviewsRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@Tag(name = "ProductReview", description = "상품 후기 관련 API 명세")
@RestController
class ProductReviewController(
    private val productReviewService: ProductReviewService
) {

    @Operation(summary = "상품 후기 조건 조회 API", description = "상품의 후기를 조건에 따라 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 후기 조건 조회 성공")
    @GetMapping("/products/{productId}/reviews")
    fun readAll(
//        @Auth loginMember: LoginMember, TODO: 비회원도 조회 가능 (회원인 경우 추천 여부 반영)
        request: ReadAllProductReviewsRequest,
        @PathVariable productId: Long,
    ): ResponseEntity<ProductReviewsResponse> {
        val responses = productReviewService.readAll(
            request.toCommand(
                productId = productId,
                null, // loginMember.memberId
            )
        )
        return ResponseEntity.ok(responses)
    }

    @Operation(summary = "상품 후기 통계 조회 API", description = "상품의 후기 통계를 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 후기 통계 조회 성공")
    @GetMapping("/products/{productId}/review-statistics")
    fun readReviewCountStatistics(
        @PathVariable productId: Long
    ): ResponseEntity<ProductReviewScoreStatistics> {
        val response = productReviewService.readReviewCountStatistics(productId)
        return ResponseEntity.ok(response)
    }
}
