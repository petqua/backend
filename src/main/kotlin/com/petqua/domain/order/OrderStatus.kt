package com.petqua.domain.order

enum class OrderStatus(
    private val description: String,
) {

    ORDER_CREATED("주문 생성"),
    ORDERED("주문 완료"),
    ORDER_CONFIRMED("주문 확인"),
    CANCELED("주문 취소"),
    PAYMENT_CONFIRMED("결제 완료"),
    DELIVERY_PREPARATION("배송 준비 중"),
    DELIVERING("배송 중"),
    DELIVERY_COMPLETED("배송 완료"),
    RETURN_REQUESTED("반품 요청"),
    RETURN_COMPLETED("반품 완료"),
    EXCHANGE_REQUESTED("교환 요청"),
    EXCHANGE_COMPLETED("교환 완료"),
    REFUND_REQUESTED("환불 요청"),
    REFUND_COMPLETED("환불 완료"),
}
