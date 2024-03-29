package com.petqua.presentation.product.category

import com.petqua.application.product.category.CategoryProductReadQuery
import com.petqua.application.product.category.CategoryReadQuery
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Sorter
import io.swagger.v3.oas.annotations.media.Schema

data class CategoryReadRequest(
    @Schema(
        description = "카테고리 어과",
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
        description = "운송 방법",
        defaultValue = "SAFETY",
        allowableValues = ["SAFETY", "COMMON", "PICK_UP"]
    )
    val deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,

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

    fun toQuery(loginMemberOrGuest: LoginMemberOrGuest): CategoryProductReadQuery {
        return CategoryProductReadQuery(
            family = family,
            species = species,
            deliveryMethod = deliveryMethod,
            sorter = sorter,
            lastViewedId = lastViewedId,
            limit = limit,
            loginMemberOrGuest = loginMemberOrGuest,
        )
    }
}
