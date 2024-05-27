package com.petqua.domain.order

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate

class OrderDynamicJpqlGenerator : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<OrderDynamicJpqlGenerator> {
        override fun newInstance(): OrderDynamicJpqlGenerator = OrderDynamicJpqlGenerator()
    }

    fun Jpql.orderNumberNotEq(orderNumber: OrderNumber?): Predicate? {
        return orderNumber?.let { path(Order::orderNumber).notEqual(it) }
    }

    fun Jpql.orderIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Order::id).lt(it) }
    }
}

