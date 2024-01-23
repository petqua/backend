package com.petqua.presentation

import com.petqua.application.ProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/product")
@RestController
class ProductController(
        private val productService: ProductService
) {
    
}
