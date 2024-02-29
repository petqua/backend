package com.petqua.application.order.dto

import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderProduct
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.domain.product.Product
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex
import java.math.BigDecimal

data class SaveOrderCommand(
    val memberId: Long,
    val shippingAddressId: Long,
    val shippingRequest: String?,
    val orderProductCommands: List<OrderProductCommand>,
    val totalAmount: BigDecimal,
)

data class OrderProductCommand(
    val productId: Long,
    val quantity: Int,
    val originalPrice: BigDecimal,
    val discountRate: Int,
    val discountPrice: BigDecimal,
    val orderPrice: BigDecimal,
    val sex: Sex,
    val additionalPrice: BigDecimal,
    val deliveryFee: BigDecimal,
    val deliveryMethod: DeliveryMethod,
) {

    fun toProductOption(): ProductOption {
        return ProductOption(
            sex = sex,
            productId = productId,
            additionalPrice = additionalPrice.setScale(2),
        )
    }

    fun toOrderProduct(
        shippingNumber: ShippingNumber,
        product: Product,
        storeName: String,
    ): OrderProduct {
        return OrderProduct(
            quantity = quantity,
            originalPrice = originalPrice.setScale(2),
            discountRate = discountRate,
            discountPrice = discountPrice.setScale(2),
            deliveryFee = deliveryFee.setScale(2),
            shippingNumber = shippingNumber,
            orderPrice = orderPrice.setScale(2),
            productId = productId,
            productName = product.name,
            thumbnailUrl = product.thumbnailUrl,
            storeId = product.storeId,
            storeName = storeName,
            deliveryMethod = deliveryMethod,
            sex = sex,
        )
    }
}

data class SaveOrderResponse(
    val orderId: String,
    val orderName: String,
    val successUrl: String,
    val failUrl: String,
)

data class PayOrderCommand(
    val memberId: Long,
    val paymentType: TossPaymentType,
    val orderNumber: OrderNumber,
    val paymentKey: String,
    val amount: BigDecimal,
) {
    fun toPaymentConfirmRequest(): PaymentConfirmRequestToPG {
        return PaymentConfirmRequestToPG(
            orderNumber = orderNumber,
            paymentKey = paymentKey,
            amount = amount
        )
    }

    companion object {
        fun of(
            memberId: Long,
            paymentType: String,
            orderId: String,
            paymentKey: String,
            amount: BigDecimal,
        ): PayOrderCommand {
            return PayOrderCommand(
                memberId = memberId,
                paymentType = TossPaymentType.from(paymentType),
                orderNumber = OrderNumber.from(orderId),
                paymentKey = paymentKey,
                amount = amount,
            )
        }
    }
}
