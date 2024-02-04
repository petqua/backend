package com.petqua.domain.keyword

import org.springframework.data.jpa.repository.JpaRepository

interface ProductKeywordRepository : JpaRepository<ProductKeyword, Long>, ProductKeywordCustomRepository

