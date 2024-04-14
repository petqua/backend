package com.petqua.domain.order

import org.springframework.data.jpa.repository.JpaRepository

fun OrderRepository.findByOrderNumberOrThrow(
    orderNumber: OrderNumber,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${Order::class.java.name} entity 를 찾을 수 없습니다.") },
): List<Order> {
    val orders = findByOrderNumber(orderNumber)
    return orders.ifEmpty { throw exceptionSupplier() }
}

interface OrderRepository : JpaRepository<Order, Long> {

    fun findByOrderNumber(orderNumber: OrderNumber): List<Order>
}
