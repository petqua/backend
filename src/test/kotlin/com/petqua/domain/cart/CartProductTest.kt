package com.petqua.domain.cart

import com.petqua.exception.cart.CartProductException
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CartProductTest : StringSpec({

    "장바구니 상품 수정" {
        val cartProduct = CartProduct(
            memberId = 1L,
            productId = 1L,
            quantity = CartProductQuantity(5),
            isMale = true,
            deliveryMethod = DeliveryMethod.COMMON,
        )

        cartProduct.updateOptions(
            quantity = CartProductQuantity(10),
            isMale = false,
            deliveryMethod = DeliveryMethod.COMMON,
        )

        assertSoftly(cartProduct) {
            isMale shouldBe false
            quantity.quantity shouldBe 10
            deliveryMethod shouldBe DeliveryMethod.COMMON
        }
    }

    "장바구니 상품 소유자 확인" {
        val cartProduct = CartProduct(
            memberId = 1L,
            productId = 1L,
            quantity = CartProductQuantity(5),
            isMale = true,
            deliveryMethod = DeliveryMethod.COMMON,
        )

        shouldThrow<CartProductException> {
            cartProduct.validateOwner(2L)
        }
    }
})
