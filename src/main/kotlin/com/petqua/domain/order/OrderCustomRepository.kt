package com.petqua.domain.order

import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PADDING_FOR_HAS_NEXT_PAGE

private const val ORDER_PAGING_LIMIT_CEILING = 5

data class OrderPaging(
    val lastViewedId: Long? = null,
    val limit: Int = ORDER_PAGING_LIMIT_CEILING,
    val lastViewedOrderNumber: OrderNumber? = null,
) {

    companion object {
        fun of(
            lastViewedId: Long,
            limit: Int,
            lastViewedOrderNumber: OrderNumber?,
        ): OrderPaging {
            val adjustedLastViewedId = if (lastViewedId == DEFAULT_LAST_VIEWED_ID) null else lastViewedId
            val adjustedLimit = if (limit > ORDER_PAGING_LIMIT_CEILING) ORDER_PAGING_LIMIT_CEILING else limit
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
