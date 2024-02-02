package com.petqua.domain.product

import com.petqua.presentation.product.WishProductResponse

interface WishProductCustomRepository {

    fun readAllWishProductResponse(memberId: Long): List<WishProductResponse>
}
