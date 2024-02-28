package com.petqua.application.payment

import com.petqua.application.order.dto.PayOrderCommand
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.findByOrderNumberOrThrow
import com.petqua.domain.payment.tosspayment.TossPayment
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class PaymentService(
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
) {

    @Transactional(readOnly = true)
    fun validateAmount(command: PayOrderCommand) {
        val order = orderRepository.findByOrderNumberOrThrow(command.orderNumber) {
            OrderException(OrderExceptionType.ORDER_NOT_FOUND)
        }
        order.validateAmount(command.amount)
    }

    fun save(tossPayment: TossPayment): TossPayment {
        return paymentRepository.save(tossPayment)
    }
}
