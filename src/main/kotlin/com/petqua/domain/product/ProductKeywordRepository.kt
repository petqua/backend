package com.petqua.domain.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductKeywordRepository : JpaRepository<ProductKeyword, Long>, ProductKeywordCustomRepository

