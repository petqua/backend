package com.petqua.test.fixture

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.common.domain.Money
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderProduct
import com.petqua.domain.order.OrderShippingAddress
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.product.Product
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.presentation.order.dto.OrderProductRequest
import com.petqua.presentation.order.dto.SaveOrderRequest
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

private const val DEFAULT_SCALE = 2

fun order(
    id: Long = 0L,
    memberId: Long = 0L,
    orderNumber: OrderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
    orderName: OrderName = OrderName("상품1"),
    receiver: String = "receiver",
    phoneNumber: String = "010-1234-5678",
    zipCode: Int = 12345,
    address: String = "서울시 강남구 역삼동 99번길",
    detailAddress: String = "101동 101호",
    requestMessage: String? = null,
    quantity: Int = 1,
    originalPrice: BigDecimal = ONE,
    discountRate: Int = 0,
    discountPrice: BigDecimal = originalPrice,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
    shippingNumber: ShippingNumber = ShippingNumber(""),
    orderPrice: BigDecimal = discountPrice,
    productId: Long = 0L,
    productName: String = "orderProduct",
    thumbnailUrl: String = "image.url",
    storeId: Long = 0L,
    storeName: String = "storeName",
    deliveryMethod: DeliveryMethod = SAFETY,
    sex: Sex = FEMALE,
    totalAmount: BigDecimal = orderPrice + deliveryFee,
): Order {
    return Order(
        id = id,
        memberId = memberId,
        orderNumber = orderNumber,
        orderName = orderName,
        orderShippingAddress = orderShippingAddress(
            receiver = receiver,
            phoneNumber = phoneNumber,
            zipCode = zipCode,
            address = address,
            detailAddress = detailAddress,
            requestMessage = requestMessage,
        ),
        orderProduct = orderProduct(
            quantity = quantity,
            originalPrice = originalPrice,
            discountRate = discountRate,
            discountPrice = discountPrice,
            deliveryFee = deliveryFee,
            shippingNumber = shippingNumber,
            orderPrice = orderPrice,
            productId = productId,
            productName = productName,
            thumbnailUrl = thumbnailUrl,
            storeId = storeId,
            storeName = storeName,
            deliveryMethod = deliveryMethod,
            sex = sex,
        ),
        totalAmount = Money.from(totalAmount),
    )
}

fun orderShippingAddress(
    receiver: String = "receiver",
    phoneNumber: String = "010-1234-5678",
    zipCode: Int = 12345,
    address: String = "서울시 강남구 역삼동 99번길",
    detailAddress: String = "101동 101호",
    requestMessage: String?,
): OrderShippingAddress {
    return OrderShippingAddress(
        receiver = receiver,
        phoneNumber = phoneNumber,
        zipCode = zipCode,
        address = address,
        detailAddress = detailAddress,
        requestMessage = requestMessage,
    )
}

fun orderProduct(
    quantity: Int = 1,
    originalPrice: BigDecimal = ONE,
    discountRate: Int = 0,
    discountPrice: BigDecimal = originalPrice,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
    shippingNumber: ShippingNumber = ShippingNumber(""),
    orderPrice: BigDecimal = discountPrice,
    productId: Long = 0L,
    productName: String = "orderProduct",
    thumbnailUrl: String = "image.url",
    storeId: Long = 0L,
    storeName: String = "storeName",
    deliveryMethod: DeliveryMethod = SAFETY,
    sex: Sex = FEMALE,
): OrderProduct {
    return OrderProduct(
        quantity = quantity,
        originalPrice = Money.from(originalPrice),
        discountRate = discountRate,
        discountPrice = Money.from(discountPrice),
        deliveryFee = Money.from(deliveryFee),
        shippingNumber = shippingNumber,
        orderPrice = Money.from(orderPrice),
        productId = productId,
        productName = productName,
        thumbnailUrl = thumbnailUrl,
        storeId = storeId,
        storeName = storeName,
        deliveryMethod = deliveryMethod,
        sex = sex,
    )
}

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
        totalAmount = Money.from(totalAmount),
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
        originalPrice = Money.from(originalPrice),
        discountRate = discountRate,
        discountPrice = Money.from(discountPrice),
        orderPrice = Money.from(orderPrice),
        sex = sex,
        additionalPrice = Money.from(additionalPrice),
        deliveryFee = Money.from(deliveryFee),
        deliveryMethod = deliveryMethod,
    )
}

fun saveOrderRequest(
    shippingAddressId: Long? = null,
    shippingRequest: String? = null,
    orderProductRequests: List<OrderProductRequest>,
    totalAmount: Money,
): SaveOrderRequest {
    return SaveOrderRequest(
        shippingAddressId = shippingAddressId,
        shippingRequest = shippingRequest,
        orderProductRequests = orderProductRequests,
        totalAmount = totalAmount
    )
}

fun orderProductRequest(
    productId: Long,
    storeId: Long,
    quantity: Int = 1,
    originalPrice: Money = Money.from(ONE),
    discountRate: Int = 0,
    discountPrice: Money = originalPrice,
    orderPrice: Money = Money.from(ONE),
    sex: String = FEMALE.name,
    additionalPrice: Money = Money.from(ZERO),
    deliveryFee: Money = Money.from(3000),
    deliveryMethod: String = COMMON.name,
): OrderProductRequest {
    return OrderProductRequest(
        productId = productId,
        storeId = storeId,
        quantity = quantity,
        originalPrice = originalPrice,
        discountRate = discountRate,
        discountPrice = discountPrice,
        orderPrice = orderPrice,
        sex = sex,
        additionalPrice = additionalPrice,
        deliveryFee = deliveryFee,
        deliveryMethod = deliveryMethod
    )
}

fun orderProductRequest(
    product: Product,
    productOption: ProductOption,
    quantity: Int = 1,
    sex: Sex,
    deliveryFee: Money,
    deliveryMethod: DeliveryMethod,
): OrderProductRequest {
    return OrderProductRequest(
        productId = product.id,
        storeId = product.storeId,
        quantity = quantity,
        originalPrice = product.price,
        discountRate = product.discountRate,
        discountPrice = product.discountPrice,
        orderPrice = product.price + productOption.additionalPrice,
        sex = sex.name,
        additionalPrice = productOption.additionalPrice,
        deliveryFee = deliveryFee,
        deliveryMethod = deliveryMethod.name
    )
}
