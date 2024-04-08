package com.petqua.application.order

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.application.payment.infra.PaymentGatewayClient
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
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
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.EMPTY_SHIPPING_ADDRESS
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.STORE_NOT_FOUND
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
import com.petqua.exception.product.ProductException
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
        val productIds = command.orderProductCommands.map { it.productId }
        val productById = productRepository.findAllByIsDeletedFalseAndIdIn(productIds).associateBy { it.id }

        validateOrderProducts(command, productById)
        val shippingAddress = findValidateShippingAddress(command.shippingAddressId, command.orderProductCommands)
        val productSnapshots = findValidateProductSnapshots(productById)

        // TODO: TODO 재고 검증
        val orders = saveOrders(command, productSnapshots, shippingAddress)
        orderPaymentRepository.saveAll(orders.map { OrderPayment.from(it) })
        return SaveOrderResponse(
            orderId = orders.first().orderNumber.value,
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
        val productSnapshots = productSnapshotRepository.findAllByProductIdIn(productIds).associateBy { it.productId }
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
                isAbleToCancel = true,
                status = ORDER_CREATED,
                totalAmount = command.totalAmount,
            )
        }
        return orderRepository.saveAll(orders)
    }
}
