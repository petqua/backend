package com.petqua.exception.payment

import com.petqua.exception.payment.FailPaymentExceptionType.INVALID_CODE
import java.util.Locale

enum class FailPaymentCode {

    PAY_PROCESS_CANCELED,
    PAY_PROCESS_ABORTED,
    REJECT_CARD_COMPANY,
    ;

    companion object {
        fun from(name: String): FailPaymentCode {
            return enumValues<FailPaymentCode>().find { it.name == name.uppercase(Locale.ENGLISH) }
                ?: throw FailPaymentException(INVALID_CODE)
        }
    }
}
