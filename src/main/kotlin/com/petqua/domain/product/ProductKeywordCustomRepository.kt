package com.petqua.domain.product

import com.petqua.application.product.dto.ProductKeywordResponse

interface ProductKeywordCustomRepository {

    fun findBySearch(word: String, limit: Int): List<ProductKeywordResponse>
}
