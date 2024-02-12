package com.petqua.domain.product.detail

import org.springframework.data.jpa.repository.JpaRepository

interface ProductInfoRepository : JpaRepository<ProductInfo, Long> {
}
