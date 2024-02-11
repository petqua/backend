package com.petqua.presentation.product.dto

import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import io.swagger.v3.oas.annotations.media.Schema

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
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
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

data class ProductSearchRequest(
    @Schema(
        description = "검색어",
        example = "구피"
    )
    val word: String,

    @Schema(
        description = "안전 운송 필터 여부",
        example = "true"
    )
    val canDeliverSafely: Boolean?,

    @Schema(
        description = "일반 운송 필터 여부",
        example = "false"
    )
    val canDeliverCommonly: Boolean?,

    @Schema(
        description = "직접 수령 필터 여부",
        example = "false"
    )
    val canPickUp: Boolean?,

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
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toQuery(): ProductSearchQuery {
        return ProductSearchQuery(
            word = word,
            canDeliverSafely = canDeliverSafely,
            canDeliverCommonly = canDeliverCommonly,
            canPickUp = canPickUp,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}

data class ProductKeywordRequest(
    @Schema(
        description = "마지막으로 조회한 상품의 Id",
        example = "1"
    )
    val word: String,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,
) {

    fun toQuery(): ProductKeywordQuery {
        return ProductKeywordQuery(
            word = word,
            limit = limit,
        )
    }
}
