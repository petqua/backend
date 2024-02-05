package com.petqua.domain.product

import com.petqua.domain.product.dto.ProductPaging
import com.petqua.presentation.product.WishProductResponse

interface WishProductCustomRepository {

    fun readAllWishProductResponse(memberId: Long, paging: ProductPaging): List<WishProductResponse>
}
