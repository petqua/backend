package com.petqua.domain.payment.tosspayment

import org.springframework.data.jpa.repository.JpaRepository

interface TossPaymentRepository : JpaRepository<TossPayment, Long> {
}
