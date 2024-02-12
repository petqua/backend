package com.petqua.domain.product.detail

import org.springframework.data.jpa.repository.JpaRepository

interface ProductImageRepository : JpaRepository<ProductImage, Long> {

    fun findProductImagesByProductId(productId: Long): List<ProductImage>
}
