package com.petqua.domain.order

import org.springframework.data.jpa.repository.JpaRepository

fun OrderRepository.findByOrderNumberOrThrow(
    orderNumber: OrderNumber,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${Order::class.java.name} entity 를 찾을 수 없습니다.") },
): List<Order> {
    val orders = findByOrderNumber(orderNumber)
    return orders.ifEmpty { throw exceptionSupplier() }
}

interface OrderRepository : JpaRepository<Order, Long>, OrderCustomRepository {

    fun findByOrderNumber(orderNumber: OrderNumber): List<Order>

//    @Query(
//        """
//        SELECT o
//        FROM Order o
//        WHERE o.memberId = :memberId AND o.orderNumber
//        IN (
//            SELECT DISTINCT o2.orderNumber
//            FROM Order o2
//            WHERE o2.memberId = :memberId
//            AND o2.id < :#{#paging.lastViewedId}
//            ORDER BY o2.createdAt DESC
//            LIMIT :#{#paging.limit}
//        )
//        """
//    )
//    fun findRecentDateOrdersByMemberId(memberId: Long, paging: OrderPaging): List<Order>
}
