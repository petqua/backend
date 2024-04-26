package com.petqua.presentation.product

import com.petqua.application.product.dto.ProductReviewStatisticsResponse
import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.review.ProductReviewFacadeService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.presentation.product.dto.CreateReviewRequest
import com.petqua.presentation.product.dto.ReadAllProductReviewsRequest
import com.petqua.presentation.product.dto.UpdateReviewRecommendationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "ProductReview", description = "상품 후기 관련 API 명세")
@RestController
class ProductReviewController(
    private val productReviewFacadeService: ProductReviewFacadeService,
) {

    @Operation(summary = "상품 후기 작성 API", description = "상품의 후기를 작성합니다")
    @ApiResponse(responseCode = "201", description = "상품 후기 작성 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @PostMapping(
        value = ["/products/{productId}/reviews"],
        consumes = [MULTIPART_FORM_DATA_VALUE],
        produces = [APPLICATION_JSON_VALUE]
    )
    fun create(
        @Auth loginMember: LoginMember,
        @PathVariable productId: Long,
        @ModelAttribute request: CreateReviewRequest,
    ): ResponseEntity<ProductReviewsResponse> {
        val command = request.toCommand(
            memberId = loginMember.memberId,
            productId = productId,
            images = request.images
        )
        productReviewFacadeService.create(command)
        return ResponseEntity.status(CREATED).build()
    }

    @Operation(summary = "상품 후기 조건 조회 API", description = "상품의 후기를 조건에 따라 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 후기 조건 조회 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @GetMapping("/products/{productId}/reviews")
    fun readAll(
        @Auth loginMemberOrGuest: LoginMemberOrGuest,
        request: ReadAllProductReviewsRequest,
        @PathVariable productId: Long,
    ): ResponseEntity<ProductReviewsResponse> {
        val responses = productReviewFacadeService.readAll(
            request.toCommand(
                productId = productId,
                loginMemberOrGuest = loginMemberOrGuest,
            )
        )
        return ResponseEntity.ok(responses)
    }

    @Operation(summary = "상품 후기 통계 조회 API", description = "상품의 후기 통계를 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 후기 통계 조회 성공")
    @GetMapping("/products/{productId}/review-statistics")
    fun readReviewCountStatistics(
        @PathVariable productId: Long,
    ): ResponseEntity<ProductReviewStatisticsResponse> {
        val response = productReviewFacadeService.readReviewCountStatistics(productId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "상품 후기 추천 토글 API", description = "상품 후기의 추천 여부를 토글합니다")
    @ApiResponse(responseCode = "204", description = "상품 후기 추천 토글 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @PostMapping("/product-reviews/recommendation")
    fun updateRecommendation(
        @Auth loginMember: LoginMember,
        @RequestBody request: UpdateReviewRecommendationRequest,
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        productReviewFacadeService.updateReviewRecommendation(command)
        return ResponseEntity
            .noContent()
            .build()
    }
}
