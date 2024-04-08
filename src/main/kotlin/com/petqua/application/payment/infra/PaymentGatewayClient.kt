package com.petqua.application.payment.infra

import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.PaymentResponseFromPG

interface PaymentGatewayClient {

    fun confirmPayment(paymentConfirmRequestToPG: PaymentConfirmRequestToPG): PaymentResponseFromPG
}
