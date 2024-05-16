package com.petqua.domain.order

import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING

data class OrderPaging(
    val lastViewedId: Long? = null,
    val limit: Int = PAGING_LIMIT_CEILING,
    val lastViewedOrderNumber: OrderNumber? = null,
) {

    companion object {
        fun of(
            lastViewedId: Long,
            limit: Int,
            lastViewedOrderNumber: OrderNumber?,
        ): OrderPaging {
            val adjustedLastViewedId = if (lastViewedId == DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > PAGING_LIMIT_CEILING) PAGING_LIMIT_CEILING else limit
            return OrderPaging(adjustedLastViewedId, adjustedLimit + PADDING_FOR_HAS_NEXT_PAGE, lastViewedOrderNumber)
        }
    }
}

interface OrderCustomRepository {

    fun findOrdersByMemberId(
        memberId: Long,
        orderPaging: OrderPaging,
    ): List<Order>
}
