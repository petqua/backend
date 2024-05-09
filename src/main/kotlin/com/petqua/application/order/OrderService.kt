package com.petqua.application.order

import com.petqua.application.order.dto.OrderDetailReadQuery
import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.application.payment.infra.PaymentGatewayClient
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.util.getOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderShippingAddress
import com.petqua.domain.order.ShippingAddress
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.order.findByOrderNumberOrThrow
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.ProductSnapshotRepository
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.EMPTY_SHIPPING_ADDRESS
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.STORE_NOT_FOUND
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.presentation.order.dto.OrderDetailResponse
import com.petqua.presentation.order.dto.OrderProductResponse
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
        val productIds = command.orderProductCommands.map { it.productId }
        val productById = productRepository.findAllByIsDeletedFalseAndIdIn(productIds).associateBy { it.id }

        validateOrderProducts(command, productById)
        val shippingAddress = findValidateShippingAddress(command.shippingAddressId, command.orderProductCommands)
        val productSnapshots = findValidateProductSnapshots(productById)

        // TODO: TODO 재고 검증
        val orders = saveOrders(command, productSnapshots, shippingAddress)
        orderPaymentRepository.saveAll(orders.map { OrderPayment.from(it) })
        return SaveOrderResponse(
            orderNumber = orders.first().orderNumber.value,
            orderName = orders.first().orderName.value,
        )
    }

    private fun validateOrderProducts(command: SaveOrderCommand, productById: Map<Long, Product>) {
        val productOptions = productOptionRepository.findByProductIdIn(productById.keys.toList())
        val orderProductsValidator = OrderProductsValidator(productById, productOptions.toSet())
        orderProductsValidator.validate(command.totalAmount, command.orderProductCommands)
    }

    private fun findValidateShippingAddress(
        shippingAddressId: Long?,
        orderProductCommands: List<OrderProductCommand>
    ): ShippingAddress? {
        if (shippingAddressId == null) {
            throwExceptionWhen(orderProductCommands.any { it.deliveryMethod != PICK_UP }) {
                OrderException(EMPTY_SHIPPING_ADDRESS)
            }
            return null
        }

        return shippingAddressRepository.findByIdOrThrow(shippingAddressId) {
            ShippingAddressException(NOT_FOUND_SHIPPING_ADDRESS)
        }
    }

    private fun findValidateProductSnapshots(productById: Map<Long, Product>): Map<Long, ProductSnapshot> {
        val productIds = productById.keys.toList()
        val products = productById.values.toList()
        val productSnapshots =
            productSnapshotRepository.findLatestAllByProductIdIn(productIds).associateBy { it.productId }
        products.forEach { product ->
            productSnapshots[product.id]?.takeIf { it.isProductDetailsMatching(product) }
                ?: throw ProductException(NOT_FOUND_PRODUCT)
        }
        return productSnapshots
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
                totalAmount = command.totalAmount,
            )
        }
        return orderRepository.saveAll(orders)
    }

    fun readDetail(query: OrderDetailReadQuery): OrderDetailResponse {
        val orders = orderRepository.findByOrderNumberOrThrow(query.orderNumber) { OrderException(ORDER_NOT_FOUND) }
        orders.forEach { it.validateOwner(query.memberId) }
        val orderProductResponses = orderProductResponsesFromOrders(orders)
        val representativeOrder = orders[0]
        return OrderDetailResponse(
            orderNumber = representativeOrder.orderNumber.value,
            orderedAt = representativeOrder.createdAt,
            orderProducts = orderProductResponses,
            totalAmount = representativeOrder.totalAmount,
        )
    }

    private fun orderProductResponsesFromOrders(orders: List<Order>): List<OrderProductResponse> {
        val statusByOrderId = orders.map { orderPaymentRepository.findOrderStatusByOrderId(it.id) }
            .associateBy { orderPayment -> orderPayment.orderId }
            .mapValues { it.value.status }
        return orders.map { OrderProductResponse(it, statusByOrderId.getOrThrow(it.id)) }
    }
}
