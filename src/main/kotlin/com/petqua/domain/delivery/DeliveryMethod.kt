package com.petqua.domain.delivery

import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.INVALID_DELIVERY_METHOD
import java.util.Locale.ENGLISH

enum class DeliveryMethod(
    val description: String,
) {

    COMMON("일반 운송"),
    SAFETY("안전 운송"),
    PICK_UP("직접 방문"),
    ;

    companion object {
        fun from(name: String): DeliveryMethod {
            return enumValues<DeliveryMethod>().find { it.name == name.uppercase(ENGLISH) }
                ?: throw CartProductException(INVALID_DELIVERY_METHOD)
        }
    }
}
