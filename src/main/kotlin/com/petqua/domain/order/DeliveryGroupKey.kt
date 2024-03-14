package com.petqua.domain.order

import com.petqua.domain.delivery.DeliveryMethod

data class DeliveryGroupKey(
    val storeId: Long,
    val deliveryMethod: DeliveryMethod,
)
