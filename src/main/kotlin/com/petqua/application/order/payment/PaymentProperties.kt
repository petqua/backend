package com.petqua.application.order.payment

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "payment.toss-payments")
data class PaymentProperties(
    val secretKey: String,
    val successUrl: String,
    val failUrl: String,
)
