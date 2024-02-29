package com.petqua.presentation.order.dto

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.PayOrderCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import java.math.BigDecimal

data class SaveOrderRequest(
    val shippingAddressId: Long,
    val shippingRequest: String?,
    val orderProductRequests: List<OrderProductRequest>,
    val totalAmount: BigDecimal,
) {

    fun toCommand(memberId: Long): SaveOrderCommand {
        return SaveOrderCommand(
            memberId = memberId,
            shippingAddressId = shippingAddressId,
            shippingRequest = shippingRequest,
            orderProductCommands = orderProductRequests.map { it.toCommand() },
            totalAmount = totalAmount,
        )
    }
}

data class OrderProductRequest(
    val productId: Long,
    val storeId: Long,
    val quantity: Int,
    val originalPrice: BigDecimal,
    val discountRate: Int,
    val discountPrice: BigDecimal,
    val orderPrice: BigDecimal,
    val sex: String,
    val additionalPrice: BigDecimal,
    val deliveryFee: BigDecimal,
    val deliveryMethod: String,
) {

    fun toCommand(): OrderProductCommand {
        return OrderProductCommand(
            productId = productId,
            quantity = quantity,
            originalPrice = originalPrice,
            discountRate = discountRate,
            discountPrice = discountPrice,
            orderPrice = orderPrice,
            sex = Sex.from(sex),
            additionalPrice = additionalPrice,
            deliveryFee = deliveryFee,
            deliveryMethod = DeliveryMethod.from(deliveryMethod),
        )
    }
}

data class PayOrderRequest(
    val paymentType: String,
    val orderId: String,
    val paymentKey: String,
    val amount: BigDecimal,
) {

    fun toCommand(): PayOrderCommand {
        return PayOrderCommand.of(
            paymentType = paymentType,
            orderId = orderId,
            paymentKey = paymentKey,
            amount = amount,
        )
    }
}
