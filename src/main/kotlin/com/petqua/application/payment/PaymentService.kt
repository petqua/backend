package com.petqua.application.payment

import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.findByOrderNumberOrThrow
import com.petqua.domain.payment.tosspayment.TossPayment
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class PaymentService(
    private val orderRepository: OrderRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    private val paymentRepository: TossPaymentRepository,
) {

    @Transactional(readOnly = true)
    fun validateAmount(command: SucceedPaymentCommand) {
        val order = orderRepository.findByOrderNumberOrThrow(command.orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        order.validateOwner(command.memberId)
        order.validateAmount(command.amount)
    }

    fun processPayment(tossPayment: TossPayment): OrderPayment {
        val order = orderRepository.findByOrderNumberOrThrow(tossPayment.orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        order.pay()

        val payment = paymentRepository.save(tossPayment)
        return orderPaymentRepository.save(
            OrderPayment(
                orderId = order.id,
                tossPaymentId = payment.id
            )
        )
    }

    fun cancelOrder(memberId: Long, orderNumber: OrderNumber) {
        val order = orderRepository.findByOrderNumberOrThrow(orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        order.validateOwner(memberId)
        order.cancel()
    }
}
