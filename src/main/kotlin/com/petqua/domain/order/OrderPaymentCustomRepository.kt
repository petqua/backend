package com.petqua.domain.order

interface OrderPaymentCustomRepository {

    fun findLatestByOrderIdOrThrow(orderId: Long, exceptionSupplier: () -> RuntimeException): OrderPayment
}
