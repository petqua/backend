package com.petqua.application.order

import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderShippingAddress
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_PRICE_NOT_MATCH
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_PRODUCT_OPTION
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val storeRepository: StoreRepository,
) {

    fun save(command: SaveOrderCommand): SaveOrderResponse {
        // TODO 상품 존재 검증
        val productIds = command.orderProductCommands.map { it.productId }
        val productById = productRepository.findAllByIsDeletedFalseAndIdIn(productIds).associateBy { it.id }
        val products = productById.map { it.value }

        throwExceptionWhen(products.size != productIds.size) { OrderException(PRODUCT_NOT_FOUND) }

        // TODO 상품 유효성 검증 - 올바른 옵션 매칭인가?
        val productOptions = productOptionRepository.findByProductIdIn(productIds)

        command.orderProductCommands.forEach { productOptionCommand ->
            productOptions.find { it.productId == productOptionCommand.productId }?.let {
                throwExceptionWhen(!it.isSame(productOptionCommand.toProductOption())) {
                    ProductException(INVALID_PRODUCT_OPTION)
                }
            } ?: throw ProductException(INVALID_PRODUCT_OPTION)
        }

        // TODO 배송지 존재 검증
        val shippingAddress = shippingAddressRepository.findByIdOrThrow(
            command.shippingAddressId, ShippingAddressException(
                ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
            )
        )

        // TODO 총 가격 검증
        // 1. 상품 가격
        command.orderProductCommands.forEach { productCommand ->
            val product = productById[productCommand.productId]
                ?: throw OrderException(PRODUCT_NOT_FOUND)
            val productOption = productOptions.find { it.productId == product.id }
                ?: throw ProductException(INVALID_PRODUCT_OPTION)

            throwExceptionWhen(
                productCommand.orderPrice != (product.discountPrice + productOption.additionalPrice) * productCommand.quantity.toBigDecimal()
                        || productCommand.deliveryFee != product.getDeliveryFee(productCommand.deliveryMethod)
            ) {
                OrderException(
                    ORDER_PRICE_NOT_MATCH
                )
            }
        }

        // 3. 총 배송비 검증 (스토어로 묶인 뒤 배송비 검증)
        val groupBy = products.groupBy { product ->
            Pair(
                product.storeId,
                command.orderProductCommands.find { it.productId == product.id }?.deliveryMethod
                    ?: throw OrderException(PRODUCT_NOT_FOUND)
            )
        }
        val orderDeliveryFee = groupBy.map { (storeDeliveryMethod, products) ->
            val deliveryMethod = storeDeliveryMethod.second
            products.first().getDeliveryFee(deliveryMethod).toInt()
        }.sum()

        // 4. 총 결제 금액 검증
        throwExceptionWhen(command.totalAmount != orderDeliveryFee.toBigDecimal() + command.orderProductCommands.sumOf { it.orderPrice }) {
            OrderException(
                ORDER_PRICE_NOT_MATCH
            )
        }

        // TODO: TODO 재고 검증

        val storesById = storeRepository.findByIdIn(products.map { it.storeId }).associateBy { it.id }
        val orderNumber = OrderNumber.generate()
        val orderName = OrderName.from(products)
        // TODO 주문 저장 로직
        val orders = command.orderProductCommands.map { productCommand ->
            val product = productById[productCommand.productId]
                ?: throw OrderException(PRODUCT_NOT_FOUND)

            Order(
                memberId = command.memberId,
                orderNumber = orderNumber,
                orderName = orderName,
                orderShippingAddress = OrderShippingAddress.from(shippingAddress, command.shippingRequest),
                orderProduct = productCommand.toOrderProduct(
                    shippingNumber = ShippingNumber.of(product.storeId, productCommand.deliveryMethod, orderNumber),
                    product = product,
                    storeName = storesById[product.storeId]?.name ?: throw OrderException(PRODUCT_NOT_FOUND),
                ),
                isAbleToCancel = true,
                status = ORDER_CREATED,
                totalAmount = command.totalAmount,
            )
        }
        orderRepository.saveAll(orders)

        return SaveOrderResponse(
            orderId = orders.first().orderNumber.value,
            orderName = orders.first().orderName.value,
            successUrl = "successUrl",
            failUrl = "failUrl",
        )
    }
}
