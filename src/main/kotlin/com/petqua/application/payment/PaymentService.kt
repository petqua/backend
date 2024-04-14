package com.petqua.application.payment

import com.petqua.domain.order.OrderGroup
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.findByOrderNumberOrThrow
import com.petqua.domain.order.findLatestByOrderIdOrThrow
import com.petqua.domain.order.saveOrThrowOnIntegrityViolation
import com.petqua.domain.payment.tosspayment.TossPayment
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderPaymentException
import com.petqua.exception.order.OrderPaymentExceptionType.FAIL_SAVE
import com.petqua.exception.order.OrderPaymentExceptionType.ORDER_PAYMENT_NOT_FOUND
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
        val orders = orderRepository.findByOrderNumberOrThrow(command.orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        val orderGroup = OrderGroup(orders)
        orderGroup.validateOwner(command.memberId)
        orderGroup.validateAmount(command.amount)
    }

    fun processPayment(tossPayment: TossPayment) {
        val orders = orderRepository.findByOrderNumberOrThrow(tossPayment.orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        val orderGroup = OrderGroup(orders)
        val payment = paymentRepository.save(tossPayment)
        orderGroup.ordersWithSameOrderNumber.map {
            val orderPayment =
                orderPaymentRepository.findLatestByOrderIdOrThrow(it.id) {
                    OrderPaymentException(ORDER_PAYMENT_NOT_FOUND)
                }
            orderPaymentRepository.saveOrThrowOnIntegrityViolation(orderPayment.pay(payment.id)) {
                OrderPaymentException(FAIL_SAVE)
            }
        }
    }

    fun cancelOrder(memberId: Long, orderNumber: OrderNumber) {
        val orders = orderRepository.findByOrderNumberOrThrow(orderNumber) {
            OrderException(ORDER_NOT_FOUND)
        }
        val orderGroup = OrderGroup(orders)
        orderGroup.validateOwner(memberId)

        orderGroup.ordersWithSameOrderNumber.map {
            val orderPayment = orderPaymentRepository.findLatestByOrderIdOrThrow(it.id) {
                OrderPaymentException(ORDER_PAYMENT_NOT_FOUND)
            }
            orderPaymentRepository.saveOrThrowOnIntegrityViolation(orderPayment.cancel()) {
                OrderPaymentException(FAIL_SAVE)
            }
        }
    }
}
