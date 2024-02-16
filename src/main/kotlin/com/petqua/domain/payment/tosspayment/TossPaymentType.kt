package com.petqua.domain.payment.tosspayment

enum class TossPaymentType(
    private val description: String,
) {

    NORMAL("일반 결제"),
    BILLING("자동 결제"),
    BRAND_PAY("브랜드 페이"),
}
