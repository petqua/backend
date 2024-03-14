package com.petqua.presentation.order.dto

import com.petqua.application.order.dto.OrderProductCommand
import com.petqua.application.order.dto.SaveOrderCommand
import com.petqua.common.domain.Money
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import io.swagger.v3.oas.annotations.media.Schema

data class SaveOrderRequest(
    @Schema(
        description = "운송지 id",
        example = "1"
    )
    val shippingAddressId: Long,

    @Schema(
        description = "운송지 요청 사항",
        example = "경비실에 맡겨주세요."
    )
    val shippingRequest: String?,

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
