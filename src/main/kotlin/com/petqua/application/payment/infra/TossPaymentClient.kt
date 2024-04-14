package com.petqua.application.payment.infra

import com.fasterxml.jackson.databind.ObjectMapper
import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.PaymentErrorResponseFromPG
import com.petqua.application.payment.PaymentResponseFromPG
import com.petqua.common.util.BasicAuthUtils
import com.petqua.exception.payment.PaymentException
import com.petqua.exception.payment.PaymentExceptionType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@ConfigurationProperties(prefix = "payment.toss-payments")
data class PaymentProperties(
    val secretKey: String,
    val successUrl: String,
    val failUrl: String,
)

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
        } catch (e: WebClientResponseException) {
            val errorResponse = objectMapper.readValue(e.responseBodyAsString, PaymentErrorResponseFromPG::class.java)
            throw PaymentException(PaymentExceptionType.from(errorResponse.code))
        }
    }
}
