package com.petqua.presentation.order.dto

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.OrderReadQuery
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.common.domain.Money
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.order.Order
import com.petqua.domain.order.OrderStatus
import com.petqua.domain.product.option.Sex
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class SaveOrderRequest(
    @Schema(
        description = "운송지 id",
        example = "1"
    )
    val shippingAddressId: Long?,

    @Schema(
        description = "운송지 요청 사항",
        example = "경비실에 맡겨주세요."
    )
    val shippingRequest: String?,

    @Schema(
        description = "주문 상품 목록",
        example = "[{\"productId\": 1, \"storeId\": 1, \"quantity\": 2, \"originalPrice\": 40000, \"discountRate\": 50, \"discountPrice\": 20000, \"orderPrice\": 22000, \"sex\": \"FEMALE\", \"additionalPrice\": 2000, \"deliveryFee\": 5000, \"deliveryMethod\": \"SAFETY\"}]"
    )
    val orderProductRequests: List<OrderProductRequest>,

    @Schema(
        description = "주문 총금액",
        example = "49000"
    )
    val totalAmount: Money,
) {

    fun toCommand(memberId: Long): SaveOrderCommand {
        return SaveOrderCommand(
            memberId = memberId,
            shippingAddressId = shippingAddressId,
            shippingRequest = shippingRequest,
            orderProductCommands = orderProductRequests.map { it.toCommand() },
            totalAmount = totalAmount,
        )
    }
}

data class OrderProductRequest(
    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상점 id",
        example = "1"
    )
    val storeId: Long,

    @Schema(
        description = "상품 수량",
        example = "2"
    )
    val quantity: Int,

    @Schema(
        description = "상품 기존 금액",
        example = "40000"
    )
    val originalPrice: Money,

    @Schema(
        description = "할인율",
        example = "50"
    )
    val discountRate: Int,

    @Schema(
        description = "할인 가격(판매 가격)",
        example = "20000"
    )
    val discountPrice: Money,

    @Schema(
        description = "주문한 상품 가격(할인 가격 + 옵션 추가 금액)",
        example = "22000"
    )
    val orderPrice: Money,

    @Schema(
        description = "성별",
        defaultValue = "FEMALE",
        allowableValues = ["FEMALE", "MALE", "HERMAPHRODITE"]
    )
    val sex: String,

    @Schema(
        description = "옵션 추가 금액",
        example = "2000"
    )
    val additionalPrice: Money,

    @Schema(
        description = "운송비",
        example = "5000",
    )
    val deliveryFee: Money,

    @Schema(
        description = "운송 방법",
        defaultValue = "SAFETY",
        allowableValues = ["SAFETY", "COMMON", "PICK_UP"]
    )
    val deliveryMethod: String,
) {

    fun toCommand(): OrderProductCommand {
        return OrderProductCommand(
            productId = productId,
            quantity = quantity,
            originalPrice = originalPrice,
            discountRate = discountRate,
            discountPrice = discountPrice,
            orderPrice = orderPrice,
            sex = Sex.from(sex),
            additionalPrice = additionalPrice,
            deliveryFee = deliveryFee,
            deliveryMethod = DeliveryMethod.from(deliveryMethod),
        )
    }
}

data class OrderDetailResponse(
    @Schema(
        description = "주문 번호",
        example = "20210901000001"
    )
    val orderNumber: String,

    @Schema(
        description = "주문 시기",
        example = "2021-09-01T00:00:00"
    )
    val orderedAt: LocalDateTime,

    @Schema(
        description = "주문 상품 목록",
    )
    val orderProducts: List<OrderProductResponse>,

    @Schema(
        description = "주문 총 금액",
        example = "44000"
    )
    val totalAmount: Money,
)

data class OrderProductResponse(

    @Schema(
        description = "주문 id",
        example = "1"
    )
    val orderId: Long,

    @Schema(
        description = "주문 상태",
        example = "ORDERED"
    )
    val orderStatus: String,

    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상점 id",
        example = "1"
    )
    val storeId: Long,

    @Schema(
        description = "상점 이름",
        example = "펫쿠아"
    )
    val storeName: String,

    @Schema(
        description = "상품 대표 사진",
        example = "[\"https://images.com/product/1.jpg\"]"
    )
    val thumbnail: String,

    @Schema(
        description = "상품 명",
        example = "구피"
    )
    val productName: String,

    @Schema(
        description = "상품 수량",
        example = "2"
    )
    val quantity: Int,

    @Schema(
        description = "성별",
        example = "MALE"
    )
    val sex: String,

    @Schema(
        description = "주문 상품 총 가격",
        example = "44000"
    )
    val orderPrice: Money,

    @Schema(
        description = "상품 가격",
        example = "22000"
    )
    val price: Money,

    @Schema(
        description = "운송비",
        example = "5000"
    )
    val deliveryFee: Money,

    @Schema(
        description = "운송 방법",
        example = "SAFETY"
    )
    val deliveryMethod: String,
) {

    constructor(
        order: Order,
        orderStatus: OrderStatus,
    ) : this(
        orderId = order.id,
        orderStatus = orderStatus.name,
        productId = order.orderProduct.productId,
        storeId = order.orderProduct.storeId,
        storeName = order.orderProduct.storeName,
        thumbnail = order.orderProduct.thumbnailUrl,
        productName = order.orderProduct.productName,
        quantity = order.orderProduct.quantity,
        sex = order.orderProduct.sex.name,
        orderPrice = order.orderProduct.orderPrice,
        price = order.orderProduct.orderPrice,
        deliveryFee = order.orderProduct.deliveryFee,
        deliveryMethod = order.orderProduct.deliveryMethod.name,
    )
}

data class OrdersResponse(
    val orders: List<OrderDetailResponse>,

    @Schema(
        description = "다음 페이지 존재 여부",
        example = "true"
    )
    val hasNextPage: Boolean,
) {

    companion object {
        fun of(orders: List<OrderDetailResponse>, limit: Int): OrdersResponse {
            return if (orders.size > limit) {
                OrdersResponse(orders.dropLast(1), hasNextPage = true)
            } else {
                OrdersResponse(orders, hasNextPage = false)
            }
        }
    }
}

const val INITIAL_READ_ORDER_NUMBER = "EMPTY"

data class OrderReadRequest(
    @Schema(
        description = "마지막으로 조회한 주문의 Id. 없을 경우 -1",
        example = "1"
    )
    val lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,

    @Schema(
        description = "조회할 주문의 개수",
        defaultValue = "20"
    )
    val limit: Int = PAGING_LIMIT_CEILING,

    @Schema(
        description = "마지막으로 조회한 주문 번호. 없을 경우 null",
        example = "20210901000001"
    )
    val lastViewedOrderNumber: String,
) {

    fun toQuery(loginMember: LoginMember): OrderReadQuery {
        return OrderReadQuery.of(
            memberId = loginMember.memberId,
            lastViewedId = lastViewedId,
            limit = limit,
            lastViewedOrderNumber = adjustInitialReadOrderNumber(lastViewedOrderNumber),
        )
    }

    private fun adjustInitialReadOrderNumber(lastViewedOrderNumber: String) =
        if (lastViewedOrderNumber == INITIAL_READ_ORDER_NUMBER) {
            null
        } else {
            lastViewedOrderNumber
        }
}
