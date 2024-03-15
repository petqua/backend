package com.petqua.application.payment

import com.petqua.common.domain.Money
import com.petqua.domain.order.OrderName
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.payment.tosspayment.TossPayment
import com.petqua.domain.payment.tosspayment.TossPaymentMethod
import com.petqua.domain.payment.tosspayment.TossPaymentStatus
import com.petqua.domain.payment.tosspayment.TossPaymentType

data class PaymentConfirmRequestToPG(
    val orderNumber: OrderNumber,
    val paymentKey: String,
    val amount: Money,
)

data class PaymentResponseFromPG(
    val version: String,
    val paymentKey: String,
    val type: String,
    val orderId: String,
    val orderName: String,
    val mid: String,
    val currency: String,
    val method: String,
    val totalAmount: Money,
    val balanceAmount: Money,
    val status: String,
    val requestedAt: String,
    val approvedAt: String,
    val useEscrow: Boolean,
    val lastTransactionKey: String?,
    val suppliedAmount: Money,
    val vat: Money,
    val cultureExpense: Boolean,
    val taxFreeAmount: Money,
    val taxExemptionAmount: Money,
    val cancels: List<CancelResponseFromPG>?,
    val isPartialCancelable: Boolean,
    val card: CardResponseFromPG?,
    val virtualAccount: VirtualAccountResponseFromPG?,
    val secret: String?,
    val mobilePhone: MobilePhoneResponseFromPG?,
    val giftCertificate: GiftCertificateResponseFromPG?,
    val transfer: TransferResponseFromPG?,
    val receipt: ReceiptResponseFromPG?,
    val checkout: CheckoutResponseFromPG?,
    val easyPay: EasyPayResponseFromPG?,
    val country: String,
    val failure: FailureResponseFromPG?,
    val cashReceipt: CashReceiptResponseFromPG?,
    val cashReceipts: List<CashReceiptHistoryResponseFromPG>?,
    val discount: DiscountResponseFromPG?,
) {
    fun toPayment(): TossPayment {
        return TossPayment(
            paymentKey = paymentKey,
            orderNumber = OrderNumber.from(orderId),
            orderName = OrderName(orderName),
            method = TossPaymentMethod.valueOf(method),
            totalAmount = totalAmount,
            status = TossPaymentStatus.valueOf(status),
            requestedAt = requestedAt,
            approvedAt = approvedAt,
            useEscrow = useEscrow,
            type = TossPaymentType.valueOf(type),
        )
    }
}

data class CancelResponseFromPG(
    val cancelAmount: Money,
    val cancelReason: String,
    val taxFreeAmount: Money,
    val taxExemptionAmount: Money,
    val refundableAmount: Money,
    val easyPayDiscountAmount: Money,
    val canceledAt: String,
    val transactionKey: String,
    val receiptKey: String?,
)

data class CardResponseFromPG(
    val amount: Money,
    val issuerCode: String,
    val acquirerCode: String?,
    val number: String,
    val installmentPlanMonths: Int,
    val approveNo: String,
    val useCardPoint: Boolean,
    val cardType: String,
    val ownerType: String,
    val acquireStatus: String,
    val isInterestFree: Boolean,
    val interestPayer: String?,
)

data class VirtualAccountResponseFromPG(
    val accountType: String,
    val accountNumber: String,
    val bankCode: String,
    val customerName: String,
    val dueDate: String,
    val refundStatus: String,
    val expired: Boolean,
    val settlementStatus: String,
    val refundReceiveAccount: RefundReceiveAccountResponseFromPG?, // 결제창 띄운 시점부터 30분 동안만 조회 가능
)

data class RefundReceiveAccountResponseFromPG(
    val bankCode: String,
    val accountNumber: String,
    val holderName: String,
)

data class MobilePhoneResponseFromPG(
    val customerMobilePhone: String,
    val settlementStatus: String,
    val receiptUrl: String,
)

data class GiftCertificateResponseFromPG(
    val approveNo: String,
    val settlementStatus: String,
)

data class TransferResponseFromPG(
    val bankCode: String,
    val settlementStatus: String,
)

data class ReceiptResponseFromPG(
    val url: String,
)

data class CheckoutResponseFromPG(
    val url: String,
)

data class EasyPayResponseFromPG(
    val provider: String,
    val amount: Money,
    val discountAmount: Money,
)

data class FailureResponseFromPG(
    val code: String,
    val message: String,
)

data class CashReceiptResponseFromPG(
    val type: String,
    val receiptKey: String,
    val issueNumber: String,
    val receiptUrl: String,
    val amount: Money,
    val taxFreeAmount: Money,
)

data class CashReceiptHistoryResponseFromPG(
    val receiptKey: String,
    val orderId: String,
    val orderName: String,
    val type: String,
    val issueNumber: String,
    val receiptUrl: String,
    val businessNumber: String,
    val transactionType: String,
    val amount: Money,
    val taxFreeAmount: Money,
    val issueStatus: String,
    val failure: FailureResponseFromPG,
    val customerIdentityNumber: String,
    val requestedAt: String,
)

data class DiscountResponseFromPG(
    val amount: Money,
)

data class PaymentErrorResponseFromPG(
    val code: String,
    val message: String,
)
