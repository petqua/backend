package com.petqua.test.fake

import com.petqua.application.payment.CardResponseFromPG
import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.PaymentResponseFromPG
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.common.domain.Money
import com.petqua.domain.payment.tosspayment.TossPaymentMethod
import com.petqua.domain.payment.tosspayment.TossPaymentStatus
import com.petqua.domain.payment.tosspayment.TossPaymentType
import java.math.BigDecimal

class FakeTossPaymentsApiClient : TossPaymentsApiClient {

    override fun confirmPayment(
        credentials: String,
        paymentConfirmRequestToPG: PaymentConfirmRequestToPG,
    ): PaymentResponseFromPG {
        return PaymentResponseFromPG(
            version = "version",
            paymentKey = paymentConfirmRequestToPG.paymentKey,
            type = TossPaymentType.NORMAL.name,
            orderId = paymentConfirmRequestToPG.orderNumber.value,
            orderName = "orderName",
            mid = "mid",
            currency = "currency",
            method = TossPaymentMethod.CREDIT_CARD.name,
            totalAmount = paymentConfirmRequestToPG.amount,
            balanceAmount = paymentConfirmRequestToPG.amount,
            status = TossPaymentStatus.DONE.name,
            requestedAt = "2022-01-01T00:00:00+09:00",
            approvedAt = "2022-01-01T00:00:00+09:00",
            useEscrow = false,
            lastTransactionKey = null,
            suppliedAmount = paymentConfirmRequestToPG.amount,
            vat = Money.from(0L),
            cultureExpense = false,
            taxFreeAmount = Money.from(0L),
            taxExemptionAmount = Money.from(0L),
            cancels = null,
            isPartialCancelable = true,
            card = CardResponseFromPG(
                amount = paymentConfirmRequestToPG.amount,
                issuerCode = "issuerCode",
                acquirerCode = null,
                number = "card_number",
                installmentPlanMonths = 0,
                approveNo = "approveNo",
                useCardPoint = false,
                cardType = "신용",
                ownerType = "개인",
                acquireStatus = "READY",
                isInterestFree = false,
                interestPayer = null,
            ),
            virtualAccount = null,
            secret = null,
            mobilePhone = null,
            giftCertificate = null,
            transfer = null,
            receipt = null,
            checkout = null,
            easyPay = null,
            country = "KR",
            failure = null,
            cashReceipt = null,
            cashReceipts = null,
            discount = null,
        )
    }
}
