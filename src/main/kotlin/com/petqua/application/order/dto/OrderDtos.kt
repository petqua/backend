package com.petqua.application.order.dto

import com.petqua.common.domain.Money
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPaging
import com.petqua.domain.order.OrderProduct
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.NOT_INVALID_ORDER_READ_QUERY
import io.swagger.v3.oas.annotations.media.Schema

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
    @Schema(
        description = "주문 id",
        example = "202402211607026029E90DB030"
    )
    val orderNumber: String,

    @Schema(
        description = "주문 이름",
        example = "네온 블루 구피 외 3건"
    )
    val orderName: String,
)

data class OrderDetailReadQuery(
    val memberId: Long,
    val orderNumber: OrderNumber,
) {

    companion object {
        fun of(memberId: Long, orderNumber: String): OrderDetailReadQuery {
            return OrderDetailReadQuery(
                memberId = memberId,
                orderNumber = OrderNumber(orderNumber),
            )
        }
    }
}


data class OrderReadQuery internal constructor(
    val memberId: Long,
    val lastViewedId: Long,
    val limit: Int,
    val lastViewedOrderNumber: OrderNumber?,
) {

    companion object {
        fun of(
            memberId: Long,
            lastViewedId: Long,
            limit: Int,
            lastViewedOrderNumber: String?
        ): OrderReadQuery {
            validateLastViewedIdAndOrderNumber(lastViewedId, lastViewedOrderNumber)
            return OrderReadQuery(
                memberId = memberId,
                lastViewedId = lastViewedId,
                limit = limit,
                lastViewedOrderNumber = lastViewedOrderNumber?.let { OrderNumber(it) },
            )
        }

        private fun validateLastViewedIdAndOrderNumber(lastViewedId: Long, lastViewedOrderNumber: String?) {
            throwExceptionWhen(lastViewedId == DEFAULT_LAST_VIEWED_ID && lastViewedOrderNumber != null) {
                throw OrderException(NOT_INVALID_ORDER_READ_QUERY)
            }

            throwExceptionWhen(lastViewedId != DEFAULT_LAST_VIEWED_ID && lastViewedOrderNumber == null) {
                throw OrderException(NOT_INVALID_ORDER_READ_QUERY)
            }
        }
    }

    fun toOrderPaging(): OrderPaging {
        return OrderPaging.of(lastViewedId, limit, lastViewedOrderNumber)
    }
}
