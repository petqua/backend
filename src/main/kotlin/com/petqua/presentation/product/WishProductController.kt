package com.petqua.presentation.product

import com.petqua.application.product.WishProductService
import com.petqua.application.product.dto.DeleteWishCommand
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveWishRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        wishProductService.save(command)
        return ResponseEntity
            .status(CREATED)
            .build()
    }

    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        @RequestBody request: ReadAllWishProductRequest,
    ): ResponseEntity<WishProductsResponse> {
        val responses = wishProductService.readAll(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(responses)
    }

    @DeleteMapping("/{wishProductId}")
    fun delete(
        @Auth loginMember: LoginMember,
        @PathVariable wishProductId: Long,
    ): ResponseEntity<Void> {
        val command = DeleteWishCommand(
            memberId = loginMember.memberId,
            wishProductId = wishProductId
        )
        wishProductService.delete(command)
        return ResponseEntity
            .noContent()
            .build()
    }
}
