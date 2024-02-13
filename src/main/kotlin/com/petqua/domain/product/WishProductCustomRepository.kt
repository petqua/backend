package com.petqua.domain.product

import com.petqua.common.domain.dto.CursorBasedPagingRequest
import com.petqua.presentation.product.dto.WishProductResponse

interface WishProductCustomRepository {

    fun readAllWishProductResponse(memberId: Long, paging: CursorBasedPagingRequest): List<WishProductResponse>
}
