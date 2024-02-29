package com.petqua.application.payment.infra

import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.PaymentResponseFromPG
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange(url = "https://api.tosspayments.com")
interface TossPaymentsApiClient {

    @PostExchange(
        url = "/v1/payments/confirm",
        contentType = APPLICATION_JSON_VALUE,
    )
    fun confirmPayment(
        @RequestHeader(name = AUTHORIZATION) credentials: String,
        @RequestBody paymentConfirmRequestToPG: PaymentConfirmRequestToPG,
    ): PaymentResponseFromPG
}
