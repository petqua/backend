package com.petqua.application.order.payment

import com.petqua.common.util.BasicAuthUtils
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@EnableConfigurationProperties(PaymentProperties::class)
@Component
class TossPaymentClient(
    private val tossPaymentsApiClient: TossPaymentsApiClient,
    private val paymentProperties: PaymentProperties,
) : PaymentGatewayClient {

    override fun confirmPayment(paymentConfirmRequestToPG: PaymentConfirmRequestToPG): PaymentResponseFromPG {
        val credentials = BasicAuthUtils.encodeCredentialsWithColon(paymentProperties.secretKey)
        return tossPaymentsApiClient.confirmPayment(credentials, paymentConfirmRequestToPG)
    }

    override fun successUrl(): String {
        return paymentProperties.successUrl
    }

    override fun failUrl(): String {
        return paymentProperties.failUrl
    }
}
