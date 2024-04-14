package com.petqua.domain.order

import com.petqua.common.domain.Money

class OrderGroup(
    val ordersWithSameOrderNumber: List<Order>,
) {

    fun validateOwner(memberId: Long) {
        ordersWithSameOrderNumber[FIRST].validateOwner(memberId)
    }

    fun validateAmount(amount: Money) {
        ordersWithSameOrderNumber[FIRST].validateAmount(amount)
    }

    companion object {
        private const val FIRST = 0;
    }
}
