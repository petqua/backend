package com.petqua.test.fixture

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.option.Sex.FEMALE
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

private const val DEFAULT_SCALE = 2

fun saveOrderCommand(
    memberId: Long = 0L,
    shippingAddressId: Long = 0L,
    shippingRequest: String? = null,
    orderProductCommands: List<OrderProductCommand>,
    totalAmount: BigDecimal = ONE * orderProductCommands.size.toBigDecimal(),
): SaveOrderCommand {
    return SaveOrderCommand(
        memberId = memberId,
        shippingAddressId = shippingAddressId,
        shippingRequest = shippingRequest,
        orderProductCommands = orderProductCommands,
        totalAmount = totalAmount.setScale(DEFAULT_SCALE),
    )
}

fun orderProductCommand(
    productId: Long = 0L,
    quantity: Int = 1,
    originalPrice: BigDecimal = ONE,
    discountRate: Int = 0,
    discountPrice: BigDecimal = originalPrice,
    orderPrice: BigDecimal = ONE,
    sex: Sex = FEMALE,
    additionalPrice: BigDecimal = ZERO,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
    deliveryMethod: DeliveryMethod = COMMON,
): OrderProductCommand {
    return OrderProductCommand(
        productId = productId,
        quantity = quantity,
        originalPrice = originalPrice.setScale(DEFAULT_SCALE),
        discountRate = discountRate,
        discountPrice = discountPrice.setScale(DEFAULT_SCALE),
        orderPrice = orderPrice.setScale(DEFAULT_SCALE),
        sex = sex,
        additionalPrice = additionalPrice.setScale(DEFAULT_SCALE),
        deliveryFee = deliveryFee.setScale(DEFAULT_SCALE),
        deliveryMethod = deliveryMethod,
    )
}
