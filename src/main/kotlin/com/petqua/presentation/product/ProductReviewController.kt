package com.petqua.presentation.product

import com.petqua.application.product.dto.ProductReviewsResponse
import com.petqua.application.product.review.ProductReviewService
import com.petqua.presentation.product.dto.ReadAllProductReviewsRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductReviewController(
    private val productReviewService: ProductReviewService
) {

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
}
