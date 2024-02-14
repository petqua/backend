package com.petqua.domain.product.review

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse

interface ProductReviewCustomRepository {

    fun findAllByCondition(
        condition: ProductReviewReadCondition,
        paging: CursorBasedPaging
    ): List<ProductReviewWithMemberResponse>
}
