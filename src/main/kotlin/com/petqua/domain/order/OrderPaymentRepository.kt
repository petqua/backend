package com.petqua.domain.order

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

fun OrderPaymentRepository.findLatestByOrderIdOrThrow(
    orderId: Long,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${OrderPayment::class.java.name} entity 를 찾을 수 없습니다.") },
): OrderPayment {
    return findTopByOrderIdOrderByIdDesc(orderId) ?: throw exceptionSupplier()
}

fun OrderPaymentRepository.saveOrThrowOnIntegrityViolation(
    orderPayment: OrderPayment,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${OrderPayment::class.java.name} entity 를 저장할 수 없습니다.") },
): OrderPayment {
    try {
        return save(orderPayment)
    } catch (e: DataIntegrityViolationException) {
        throw exceptionSupplier()
    }
}

interface OrderPaymentRepository : JpaRepository<OrderPayment, Long> {

    fun findTopByOrderIdOrderByIdDesc(orderId: Long): OrderPayment?

    @Query("SELECT op FROM OrderPayment op WHERE op.orderId IN :orderIds ORDER BY op.id DESC")
    fun findLatestAllByOrderIds(orderIds: List<Long>): List<OrderPayment>

    @Query("SELECT op.id FROM OrderPayment op WHERE op.orderId = ?1 ORDER BY op.id DESC")
    fun getPrevIdByOrderId(orderId: Long): Long?

    @Query("SELECT op FROM OrderPayment op WHERE op.orderId = :orderId ORDER BY op.id DESC LIMIT 1")
    fun findOrderStatusByOrderId(orderId: Long): OrderPayment

    @Query(
        """
        SELECT op 
        FROM OrderPayment op
        WHERE op.id IN (
            SELECT MAX(op2.id) 
            FROM OrderPayment op2
            WHERE op2.orderId IN :orderIds 
            GROUP BY op2.orderId
        )
        ORDER BY op.id DESC
    """
    )
    fun findOrderStatusByOrderIds(orderIds: List<Long>): List<OrderPayment>
}
