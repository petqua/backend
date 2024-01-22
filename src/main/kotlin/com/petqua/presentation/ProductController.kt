package com.petqua.presentation

import com.petqua.application.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/product")
@RestController
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long
    ): ResponseEntity<Unit> {

        productService.getProductById(productId)
        return ResponseEntity.ok().build()
    }
}
