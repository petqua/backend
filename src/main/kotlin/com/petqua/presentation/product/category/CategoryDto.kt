package com.petqua.presentation.product.category

import com.petqua.application.product.category.CategoryReadQuery
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING
import io.swagger.v3.oas.annotations.media.Schema

data class CategoryReadRequest(
    @Schema(
        description = "카테고리 과",
        example = "송사리과"
    )
    val family: String,
) {

    fun toQuery(): CategoryReadQuery {
        return CategoryReadQuery(
            family = family,
        )
    }
}

data class ProductReadRequest(
    @Schema(
        description = "상품 조회 출처",
        example = "HOME_RECOMMENDED",
        allowableValues = ["HOME_RECOMMENDED", "HOME_NEW_ENROLLMENT"]
    )
    val sourceType: ProductSourceType = ProductSourceType.NONE,

    @Schema(
        description = "정렬 기준",
        defaultValue = "ENROLLMENT_DATE_DESC",
        allowableValues = ["SALE_PRICE_ASC", "SALE_PRICE_DESC", "REVIEW_COUNT_DESC", "ENROLLMENT_DATE_DESC"]
    )
    val sorter: Sorter = Sorter.NONE,

    @Schema(
        description = "마지막으로 조회한 상품의 Id",
        example = "1"
    )
    val lastViewedId: Long? = null,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = LIMIT_CEILING,
) {

    fun toQuery(): ProductReadQuery {
        return ProductReadQuery(
            sourceType = sourceType,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}
