package com.petqua.application.order

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.application.payment.infra.PaymentGatewayClient
import com.petqua.common.domain.Money
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
import com.petqua.domain.order.DeliveryGroupKey
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderShippingAddress
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.ShippingAddress
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.ProductSnapshotRepository
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.EMPTY_SHIPPING_ADDRESS
import com.petqua.exception.order.OrderExceptionType.ORDER_PRICE_NOT_MATCH
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.STORE_NOT_FOUND
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_PRODUCT_OPTION
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val storeRepository: StoreRepository,
    private val paymentGatewayClient: PaymentGatewayClient,
) {

    fun save(command: SaveOrderCommand): SaveOrderResponse {
        val orderProducts = getOrderProductsFrom(command)
        orderProducts.validate(command)
        validateTotalAmount(command, orderProducts.getTotalDeliveryFee(command))

        val shippingAddress = findValidateShippingAddress(command.shippingAddressId, command.orderProductCommands)
        val productSnapshots =
            productSnapshotRepository.findAllByProductIdIn(orderProducts.productIds).associateBy { it.productId }
        validateProductSnapshots(orderProducts, productSnapshots)

        // TODO: TODO 재고 검증
        val orders = saveOrders(command, productSnapshots, shippingAddress)
        orderPaymentRepository.saveAll(orders.map { OrderPayment.from(it) })
        return SaveOrderResponse(
            orderId = orders.first().orderNumber.value,
            orderName = orders.first().orderName.value,
            successUrl = paymentGatewayClient.successUrl(),
            failUrl = paymentGatewayClient.failUrl(),
        )
    }

    private fun findValidateShippingAddress(
        shippingAddressId: Long?,
        orderProductCommands: List<OrderProductCommand>
    ): ShippingAddress? {
        // TODO 배송&픽업 전략이 확정되면 변경 필요
        shippingAddressId ?: run {
            orderProductCommands.find { it.deliveryMethod != PICK_UP }
                ?: throw OrderException(EMPTY_SHIPPING_ADDRESS)
        }

        if (shippingAddressId == null) {
            return null
        }

        return shippingAddressRepository.findByIdOrThrow(shippingAddressId) {
            ShippingAddressException(NOT_FOUND_SHIPPING_ADDRESS)
        }
    }

    private fun getOrderProductsFrom(command: SaveOrderCommand): OrderProducts {
        val productIds = command.orderProductCommands.map { it.productId }
        val productById = productRepository.findAllByIsDeletedFalseAndIdIn(productIds).associateBy { it.id }
        val productOptions = productOptionRepository.findByProductIdIn(productIds)
        return OrderProducts(productById, productOptions.toSet())
    }

    private fun validateTotalAmount(command: SaveOrderCommand, totalDeliveryFee: Int) {
        throwExceptionWhen(command.totalAmount != Money.from(totalDeliveryFee.toBigDecimal() + command.orderProductCommands.sumOf { it.orderPrice.value })) {
            OrderException(
                ORDER_PRICE_NOT_MATCH
            )
        }
    }

    private fun validateProductSnapshots(orderProducts: OrderProducts, productSnapshots: Map<Long, ProductSnapshot>) {
        orderProducts.products.forEach { product ->
            productSnapshots[product.id]?.takeIf { it.isProductDetailsMatching(product) }
                ?: throw ProductException(NOT_FOUND_PRODUCT)
        }
    }

    private fun saveOrders(
        command: SaveOrderCommand,
        productSnapshotsById: Map<Long, ProductSnapshot>,
        shippingAddress: ShippingAddress?
    ): List<Order> {
        val productSnapshots = productSnapshotsById.values.toList()
        val storesById = storeRepository.findByIdIn(productSnapshots.map { it.storeId }).associateBy { it.id }
        val orderNumber = OrderNumber.generate()
        val orderName = OrderName.from(productSnapshots)
        val orders = command.orderProductCommands.map { productCommand ->
            val productSnapshot = productSnapshotsById[productCommand.productId]
                ?: throw OrderException(PRODUCT_NOT_FOUND)
            val orderShippingAddress = shippingAddress?.let { OrderShippingAddress.from(it, command.shippingRequest) }
            Order(
                memberId = command.memberId,
                orderNumber = orderNumber,
                orderName = orderName,
                orderShippingAddress = orderShippingAddress,
                orderProduct = productCommand.toOrderProduct(
                    shippingNumber = ShippingNumber.of(
                        productSnapshot.storeId,
                        productCommand.deliveryMethod,
                        orderNumber
                    ),
                    productSnapshot = productSnapshot,
                    storeName = storesById[productSnapshot.storeId]?.name ?: throw OrderException(STORE_NOT_FOUND),
                ),
                isAbleToCancel = true,
                status = ORDER_CREATED,
                totalAmount = command.totalAmount,
            )
        }
        return orderRepository.saveAll(orders)
    }
}

class OrderProducts(
    val productById: Map<Long, Product>,
    val productOptions: Set<ProductOption>,
) {
    val productIds = productById.keys.toList()
    val products = productById.values.toList()

    fun validate(command: SaveOrderCommand) {
        validateProducts(command)
        validateProductOptions(command)
        validateOrderPrices(command)
    }

    fun getTotalDeliveryFee(command: SaveOrderCommand): Int {
        return products.groupBy { it.deliveryGroupKey(command) }
            .map { it.value.first().getDeliveryFee(it.key.deliveryMethod).value.toInt() }
            .sum()
    }

    private fun validateProducts(command: SaveOrderCommand) {
        val productIds = command.orderProductCommands.map { it.productId }
        throwExceptionWhen(products.size != productIds.size) { OrderException(PRODUCT_NOT_FOUND) }
    }

    private fun validateProductOptions(command: SaveOrderCommand) {
        command.orderProductCommands.forEach { orderProductCommand ->
            productOptions.find { it.productId == orderProductCommand.productId }?.let {
                throwExceptionWhen(!it.isSame(orderProductCommand.toProductOption())) {
                    ProductException(INVALID_PRODUCT_OPTION)
                }
            } ?: throw ProductException(INVALID_PRODUCT_OPTION)
        }
    }

    private fun validateOrderPrices(command: SaveOrderCommand) {
        command.orderProductCommands.forEach { orderProductCommand ->
            val product = productById[orderProductCommand.productId]
                ?: throw OrderException(PRODUCT_NOT_FOUND)
            val productOption = productOptions.findOptionBy(orderProductCommand.productId)
            product.validatePrice(productOption, orderProductCommand)
        }
    }

    private fun Set<ProductOption>.findOptionBy(productId: Long): ProductOption {
        return find { it.productId == productId }
            ?: throw ProductException(INVALID_PRODUCT_OPTION)
    }

    private fun Product.validatePrice(option: ProductOption, command: OrderProductCommand) {
        val expectedOrderPrice = (discountPrice + option.additionalPrice) * command.quantity.toBigDecimal()
        val expectedDeliveryFee = getDeliveryFee(command.deliveryMethod)
        if (command.orderPrice != expectedOrderPrice || command.deliveryFee != expectedDeliveryFee) {
            throw OrderException(ORDER_PRICE_NOT_MATCH)
        }
    }

    private fun Product.deliveryGroupKey(command: SaveOrderCommand): DeliveryGroupKey {
        return DeliveryGroupKey(
            storeId, command.orderProductCommands.find { it.productId == id }?.deliveryMethod
                ?: throw OrderException(PRODUCT_NOT_FOUND)
        )
    }
}
