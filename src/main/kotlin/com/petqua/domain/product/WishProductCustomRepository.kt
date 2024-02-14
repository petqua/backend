package com.petqua.domain.product

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.presentation.product.dto.WishProductResponse

interface WishProductCustomRepository {

    fun readAllWishProductResponse(memberId: Long, paging: CursorBasedPaging): List<WishProductResponse>
}
