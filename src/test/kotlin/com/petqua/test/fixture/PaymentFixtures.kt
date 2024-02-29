package com.petqua.test.fixture

import com.petqua.application.order.dto.PayOrderCommand
import com.petqua.application.payment.CardResponseFromPG
import com.petqua.application.payment.PaymentResponseFromPG
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.payment.tosspayment.TossPaymentMethod.CREDIT_CARD
import com.petqua.domain.payment.tosspayment.TossPaymentStatus.DONE
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.domain.payment.tosspayment.TossPaymentType.NORMAL
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

fun payOrderCommand(
    paymentType: TossPaymentType = NORMAL,
    orderNumber: OrderNumber = OrderNumber.from("OrderNumber"),
    paymentKey: String = "paymentKey",
    amount: BigDecimal = ZERO,
): PayOrderCommand {
    return PayOrderCommand(
        paymentType = paymentType,
        orderNumber = orderNumber,
        paymentKey = paymentKey,
        amount = amount,
    )
}

fun paymentResponseFromPG(
    paymentKey: String = "paymentKey",
    orderNumber: OrderNumber = OrderNumber.from("OrderNumber"),
    totalAmount: BigDecimal = ONE,
): PaymentResponseFromPG {
    return PaymentResponseFromPG(
        version = "version",
        paymentKey = paymentKey,
        type = NORMAL.name,
        orderId = orderNumber.value,
        orderName = "orderName",
        mid = "mid",
        currency = "currency",
        method = CREDIT_CARD.name,
        totalAmount = totalAmount,
        balanceAmount = totalAmount,
        status = DONE.name,
        requestedAt = "2022-01-01T00:00:00+09:00",
        approvedAt = "2022-01-01T00:00:00+09:00",
        useEscrow = false,
        lastTransactionKey = null,
        suppliedAmount = totalAmount,
        vat = ZERO,
        cultureExpense = false,
        taxFreeAmount = ZERO,
        taxExemptionAmount = ZERO,
        cancels = null,
        isPartialCancelable = true,
        card = CardResponseFromPG(
            amount = totalAmount,
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
