package com.petqua.application.order.dto

import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderProduct
import com.petqua.domain.order.ShippingNumber
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.domain.product.Product
import com.petqua.domain.product.option.ProductOption
import com.petqua.domain.product.option.Sex
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class SaveOrderCommand(
    val memberId: Long,
    val shippingAddressId: Long,
    val shippingRequest: String?,
    val orderProductCommands: List<OrderProductCommand>,
    val totalAmount: BigDecimal,
)

data class OrderProductCommand(
    val productId: Long,
    val quantity: Int,
    val originalPrice: BigDecimal,
    val discountRate: Int,
    val discountPrice: BigDecimal,
    val orderPrice: BigDecimal,
    val sex: Sex,
    val additionalPrice: BigDecimal,
    val deliveryFee: BigDecimal,
    val deliveryMethod: DeliveryMethod,
) {

    fun toProductOption(): ProductOption {
        return ProductOption(
            sex = sex,
            productId = productId,
            additionalPrice = additionalPrice.setScale(2),
        )
    }

    fun toOrderProduct(
        shippingNumber: ShippingNumber,
        product: Product,
        storeName: String,
    ): OrderProduct {
        return OrderProduct(
            quantity = quantity,
            originalPrice = originalPrice.setScale(2),
            discountRate = discountRate,
            discountPrice = discountPrice.setScale(2),
            deliveryFee = deliveryFee.setScale(2),
            shippingNumber = shippingNumber,
            orderPrice = orderPrice.setScale(2),
            productId = productId,
            productName = product.name,
            thumbnailUrl = product.thumbnailUrl,
            storeId = product.storeId,
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

data class PayOrderCommand(
    val paymentType: TossPaymentType,
    val orderNumber: OrderNumber,
    val paymentKey: String,
    val amount: BigDecimal,
) {
    fun toPaymentConfirmRequest(): PaymentConfirmRequestToPG {
        return PaymentConfirmRequestToPG(
            orderNumber = orderNumber,
            paymentKey = paymentKey,
            amount = amount
        )
    }

    companion object {
        fun of(
            paymentType: String,
            orderId: String,
            paymentKey: String,
            amount: BigDecimal,
        ): PayOrderCommand {
            return PayOrderCommand(
                paymentType = TossPaymentType.from(paymentType),
                orderNumber = OrderNumber.from(orderId),
                paymentKey = paymentKey,
                amount = amount,
            )
        }
    }
}

data class PayOrderResponse(

    val memberNickname: String,


    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상품 이름",
        example = "알비노 풀레드 아시안 고정구피"
    )
    val productName: String,

    @Schema(
        description = "상품 판매점",
        example = "S아쿠아"
    )
    val storeName: String,

    @Schema(
        description = "상품 썸네일 이미지",
        example = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
    )
    val thumbnailUrl: String,

    @Schema(
        description = "할인 가격(판매 가격)",
        example = "21000"
    )
    val discountPrice: Int,

    val quantity: Int,

    @Schema(
        description = "배송비",
        example = "5000"
    )
    val deliveryFee: BigDecimal?,
)

data class OrderResponse(
    @Schema(
        description = "주문 번호",
        example = "202402211607026029E90DB030"
    )
    val orderNumber: String,

    @Schema(
        description = "주문자",
        example = "홍길동"
    )
    val orderer: String,

    val shippingAddress: ShippigAddressResponse,

    @Schema(
        description = "배송방법",
        example = "안전배송"
    )
    val deliveryMethod: String,

    @Schema(
        description = "배송비",
        example = "3000"
    )
    val deliveryFee: Int,

    @Schema(
        description = "주문자",
        example = "홍길동"
    )
    val paymentMethod: String,

    // (...)
)

data class ShippigAddressResponse(
    @Schema(
        description = "배송지 id",
        example = "1"
    )
    val shippingAddressId: Long,

    @Schema(
        description = "배송지 이름",
        example = "집"
    )
    val name: String,

    @Schema(
        description = "받는 사람",
        example = "홍길동"
    )
    val receiver: String,

    @Schema(
        description = "전화 번호",
        example = "010-1234-1234"
    )
    val phoneNumber: String,

    @Schema(
        description = "우편 번호",
        example = "12345"
    )
    val zipCode: Int,

    @Schema(
        description = "주소",
        example = "서울특별시 강남구 역삼동 99번길"
    )
    val address: String,

    @Schema(
        description = "상세 주소",
        example = "101동 101호"
    )
    val detailAddress: String,

    @Schema(
        description = "배송 메시지",
        example = "부재 시 경비실에 맡겨주세요"
    )
    val requestMessage: String?,
)
