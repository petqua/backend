package com.petqua.presentation.cart

import com.petqua.application.cart.CartProductService
import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.cart.dto.SaveCartProductRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RequestMapping("/carts")
@RestController
class CartProductController(
    private val cartProductService: CartProductService,
) {

    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveCartProductRequest
    ): ResponseEntity<ProductDetailResponse> {
        val command = request.toCommand(loginMember.memberId)
        val cartProductId = cartProductService.save(command)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/items/{id}")
            .buildAndExpand(cartProductId)
            .toUri()
        return ResponseEntity.created(location).build()
    }
}
