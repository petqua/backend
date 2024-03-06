package com.petqua.domain.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderPaymentRepository : JpaRepository<OrderPayment, Long>
