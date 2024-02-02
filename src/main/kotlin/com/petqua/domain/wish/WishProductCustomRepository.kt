package com.petqua.domain.wish

import com.petqua.presentation.wish.WishProductResponse

interface WishProductCustomRepository {

    fun readAllWishProductResponse(memberId: Long): List<WishProductResponse>
}
