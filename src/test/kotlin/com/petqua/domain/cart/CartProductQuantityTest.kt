package com.petqua.domain.cart

import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_OVER_MAXIMUM
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_UNDER_MINIMUM
import io.kotest.core.spec.style.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class CartProductQuantityTest : StringSpec({
    "상품 수량 값객체 생성" {
        val quantity = CartProductQuantity(5)
        assertThat(quantity.quantity).isEqualTo(5)
    }

    "최소 수량 미만인 경우 생성 실패" {
        assertThatThrownBy {
            CartProductQuantity(0)
        }.isInstanceOf(CartProductException::class.java)
            .extracting("exceptionType")
            .isEqualTo(PRODUCT_QUANTITY_UNDER_MINIMUM)
    }

    "최대 수량 초과인 경우 생성 실패" {
        assertThatThrownBy {
            CartProductQuantity(100)
        }.isInstanceOf(CartProductException::class.java)
            .extracting("exceptionType")
            .isEqualTo(PRODUCT_QUANTITY_OVER_MAXIMUM)
    }
})
