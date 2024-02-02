package com.petqua.domain.cart

import com.petqua.application.cart.dto.CartProductResponse

interface CartProductCustomRepository {

    fun findAllCartResultsByMemberId(memberId: Long): List<CartProductResponse>
}
