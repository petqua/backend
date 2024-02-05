package com.petqua.presentation.product

import com.petqua.application.product.WishProductService
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/products/wishes")
@RestController
class WishProductController(
    private val wishProductService: WishProductService
) {

    @PostMapping
    fun update(
        @Auth loginMember: LoginMember,
        @RequestBody request: UpdateWishRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        wishProductService.update(command)
        return ResponseEntity
            .noContent()
            .build()
    }

    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        request: ReadAllWishProductRequest,
    ): ResponseEntity<WishProductsResponse> {
        val responses = wishProductService.readAll(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(responses)
    }
}
