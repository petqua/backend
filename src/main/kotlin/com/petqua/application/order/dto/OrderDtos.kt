package com.petqua.application.order.dto

import com.petqua.common.domain.Money
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.order.OrderProduct
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex

data class SaveOrderCommand(
    val memberId: Long,
    val shippingAddressId: Long?,
    val shippingRequest: String?,
    val orderProductCommands: List<OrderProductCommand>,
    val totalAmount: Money,
)

data class OrderProductCommand(
    val productId: Long,
    val quantity: Int,
    val originalPrice: Money,
    val discountRate: Int,
    val discountPrice: Money,
    val orderPrice: Money,
    val sex: Sex,
    val additionalPrice: Money,
    val deliveryFee: Money,
    val deliveryMethod: DeliveryMethod,
) {

    fun toProductOption(): ProductOption {
        return ProductOption(
            sex = sex,
            productId = productId,
            additionalPrice = additionalPrice,
        )
    }

    fun toOrderProduct(
        shippingNumber: ShippingNumber,
        productSnapshot: ProductSnapshot,
        storeName: String,
    ): OrderProduct {
        return OrderProduct(
            quantity = quantity,
            originalPrice = originalPrice,
            discountRate = discountRate,
            discountPrice = discountPrice,
            deliveryFee = deliveryFee,
            shippingNumber = shippingNumber,
            orderPrice = orderPrice,
            productId = productId,
            productName = productSnapshot.name,
            thumbnailUrl = productSnapshot.thumbnailUrl,
            storeId = productSnapshot.storeId,
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
