package com.petqua.domain.wish

import com.petqua.presentation.wish.WishResponse

interface WishCustomRepository {

    fun readAllWishResponse(memberId: Long): List<WishResponse>
}
