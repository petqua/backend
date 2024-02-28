package com.petqua.application.order.payment

import com.fasterxml.jackson.databind.ObjectMapper
import com.petqua.common.util.BasicAuthUtils
import com.petqua.exception.payment.PaymentException
import com.petqua.exception.payment.PaymentExceptionType
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException

@EnableConfigurationProperties(PaymentProperties::class)
@Component
class TossPaymentClient(
    private val tossPaymentsApiClient: TossPaymentsApiClient,
    private val paymentProperties: PaymentProperties,
    private val objectMapper: ObjectMapper,
) : PaymentGatewayClient {

    override fun confirmPayment(paymentConfirmRequestToPG: PaymentConfirmRequestToPG): PaymentResponseFromPG {
        val credentials = BasicAuthUtils.encodeCredentialsWithColon(paymentProperties.secretKey)
        try {
            return tossPaymentsApiClient.confirmPayment(credentials, paymentConfirmRequestToPG)
        } catch (e: RestClientResponseException) {
            val errorResponse = objectMapper.readValue(e.responseBodyAsString, PaymentErrorResponseFromPG::class.java)
            throw PaymentException(PaymentExceptionType.from(errorResponse.code))
        }
    }

    override fun successUrl(): String {
        return paymentProperties.successUrl
    }

    override fun failUrl(): String {
        return paymentProperties.failUrl
    }
}
