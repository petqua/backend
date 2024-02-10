package com.petqua.presentation.product.category

import com.petqua.application.product.category.CategoryProductReadQuery
import com.petqua.application.product.category.CategoryReadQuery
import com.petqua.domain.product.Sorter
import io.swagger.v3.oas.annotations.media.Schema

private const val LIMIT_CEILING = 20

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

data class CategoryProductReadRequest(
    @Schema(
        description = "카테고리 어과",
        example = "송사리과"
    )
    val family: String,

    @Schema(
        description = "카테고리 어종",
        example = "고정구피"
    )
    val species: List<String> = listOf(),

    @Schema(
        description = "안전 운송 가능 여부",
        example = "true"
    )
    val canDeliverSafely: Boolean?,

    @Schema(
        description = "일반 운송 가능 여부",
        example = "true"
    )
    val canDeliverCommonly: Boolean?,

    @Schema(
        description = "직접 수령 가능 여부",
        example = "true"
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
    val lastViewedId: Long? = null,

    @Schema(
        description = "조회할 상품 개수",
        defaultValue = "20"
    )
    val limit: Int = LIMIT_CEILING,
) {

    fun toQuery(): CategoryProductReadQuery {
        return CategoryProductReadQuery(
            family = family,
            species = species,
            canDeliverSafely = canDeliverSafely,
            canDeliverCommonly = canDeliverCommonly,
            canPickUp = canPickUp,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
        )
    }
}
