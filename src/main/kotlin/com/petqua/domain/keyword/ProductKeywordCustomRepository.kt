package com.petqua.domain.keyword

import com.petqua.application.product.dto.ProductKeywordResponse

interface ProductKeywordCustomRepository {

    fun findBySearch(word: String, limit: Int): List<ProductKeywordResponse>

    fun existsByWord(word: String): Boolean
}
