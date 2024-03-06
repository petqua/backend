package com.petqua.domain.payment.tosspayment

import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.INVALID_PAYMENT_TYPE
import java.util.Locale

enum class TossPaymentType(
    private val description: String,
) {

    NORMAL("일반 결제"),
    BILLING("자동 결제"),
    BRAND_PAY("브랜드 페이"),
    ;

    companion object {
        fun from(name: String): TossPaymentType {
            return enumValues<TossPaymentType>().find { it.name == name.uppercase(Locale.ENGLISH) }
                ?: throw OrderException(INVALID_PAYMENT_TYPE)
        }
    }
}
