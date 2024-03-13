package com.petqua.presentation.order.dto

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.common.domain.Money
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import java.math.BigDecimal

data class SaveOrderRequest(
    val shippingAddressId: Long,
    val shippingRequest: String?,
    val orderProductRequests: List<OrderProductRequest>,
    val totalAmount: Money,
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
    val originalPrice: Money,
    val discountRate: Int,
    val discountPrice: Money,
    val orderPrice: Money,
    val sex: String,
    val additionalPrice: Money,
    val deliveryFee: Money,
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
