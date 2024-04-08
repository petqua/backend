package com.petqua.application.order

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.common.domain.Money
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.order.DeliveryGroupKey
import com.petqua.domain.product.Product
import com.petqua.domain.product.option.ProductOption
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType

class OrderProductsValidator(
    private val productById: Map<Long, Product>,
    private val productOptions: Set<ProductOption>,
    private val products: List<Product>,
) {

    constructor(productById: Map<Long, Product>, productOptions: Set<ProductOption>) : this(
        productById,
        productOptions,
        productById.values.toList()
    )

    fun validate(totalAmount: Money, orderProductCommands: List<OrderProductCommand>) {
        validateProductsIsExist(orderProductCommands)
        validateProductOptionsIsExist(orderProductCommands)
        validateOrderProductPrices(orderProductCommands)
        validateTotalAmount(totalAmount, orderProductCommands)
    }

    fun validateProductsIsExist(orderProductCommands: List<OrderProductCommand>) {
        val productCommandIds = orderProductCommands.map { it.productId }.toSet()
        val productIds = productById.keys
        throwExceptionWhen(productCommandIds != productIds) { OrderException(OrderExceptionType.PRODUCT_NOT_FOUND) }
    }

    fun validateProductOptionsIsExist(orderProductCommands: List<OrderProductCommand>) {
        orderProductCommands.forEach { orderProductCommand ->
            val productOption = productOptions.findOptionBy(orderProductCommand.productId)
            throwExceptionWhen(!productOption.isSame(orderProductCommand.toProductOption())) {
                ProductException(ProductExceptionType.INVALID_PRODUCT_OPTION)
            }
        }
    }

    fun validateOrderProductPrices(orderProductCommands: List<OrderProductCommand>) {
        orderProductCommands.forEach { orderProductCommand ->
            val product = productById[orderProductCommand.productId]
                ?: throw OrderException(OrderExceptionType.PRODUCT_NOT_FOUND)
            val productOption = productOptions.findOptionBy(orderProductCommand.productId)
            validateOrderProductPrice(product, productOption, orderProductCommand)
        }
    }

    fun validateTotalAmount(inputTotalAmount: Money, orderProductCommands: List<OrderProductCommand>) {
        val totalDeliveryFee = calculateTotalDeliveryFee(orderProductCommands)
        throwExceptionWhen(inputTotalAmount != Money.from(totalDeliveryFee.toBigDecimal() + orderProductCommands.sumOf { it.orderPrice.value })) {
            OrderException(
                OrderExceptionType.ORDER_PRICE_NOT_MATCH
            )
        }
    }

    private fun validateOrderProductPrice(product: Product, option: ProductOption, command: OrderProductCommand) {
        val expectedOrderPrice = (product.discountPrice + option.additionalPrice) * command.quantity.toBigDecimal()
        val expectedDeliveryFee = product.getDeliveryFee(command.deliveryMethod)
        if (command.orderPrice != expectedOrderPrice || command.deliveryFee != expectedDeliveryFee) {
            throw OrderException(OrderExceptionType.ORDER_PRICE_NOT_MATCH)
        }
    }

    private fun calculateTotalDeliveryFee(orderProductCommands: List<OrderProductCommand>): Int {
        return products.groupBy { getDeliveryGroupKey(it, orderProductCommands) }
            .map { getDeliveryGroupFee(it) }
            .sum()
    }

    private fun getDeliveryGroupKey(
        product: Product,
        orderProductCommands: List<OrderProductCommand>
    ): DeliveryGroupKey {
        val deliveryMethod = orderProductCommands.find { it.productId == product.id }?.deliveryMethod
            ?: throw OrderException(OrderExceptionType.PRODUCT_NOT_FOUND)
        return DeliveryGroupKey(product.storeId, deliveryMethod)
    }

    private fun getDeliveryGroupFee(deliveryGroup: Map.Entry<DeliveryGroupKey, List<Product>>): Int {
        val productOfDeliveryGroup = deliveryGroup.value.first()
        val deliveryMethod = deliveryGroup.key.deliveryMethod
        return productOfDeliveryGroup.getDeliveryFee(deliveryMethod).value.toInt()
    }

    private fun Set<ProductOption>.findOptionBy(productId: Long): ProductOption {
        return find { it.productId == productId }
            ?: throw ProductException(ProductExceptionType.INVALID_PRODUCT_OPTION)
    }
}
