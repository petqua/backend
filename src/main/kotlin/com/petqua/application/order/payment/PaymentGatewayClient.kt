package com.petqua.application.order.payment

interface PaymentGatewayClient {

    fun confirmPayment(paymentConfirmRequestToPG: PaymentConfirmRequestToPG): PaymentResponseFromPG

    fun successUrl(): String

    fun failUrl(): String
}
