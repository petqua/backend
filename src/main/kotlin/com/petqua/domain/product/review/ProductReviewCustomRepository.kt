package com.petqua.domain.product.review

import com.petqua.domain.product.dto.ProductReviewPaging
import com.petqua.domain.product.dto.ProductReviewReadCondition
import com.petqua.domain.product.dto.ProductReviewWithMemberResponse

interface ProductReviewCustomRepository {

    fun findAllByCondition(
        condition: ProductReviewReadCondition,
        paging: ProductReviewPaging
    ): List<ProductReviewWithMemberResponse>
}
