package com.petqua.domain.cart

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.MALE
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
            sex = FEMALE,
            deliveryMethod = DeliveryMethod.COMMON,
            deliveryFee = 3000.toBigDecimal(),
        )

        cartProduct.updateOptions(
            quantity = CartProductQuantity(10),
            sex = MALE,
            deliveryMethod = DeliveryMethod.COMMON,
            deliveryFee = 3000.toBigDecimal(),
        )

        assertSoftly(cartProduct) {
            sex shouldBe MALE
            quantity.value shouldBe 10
            deliveryMethod shouldBe DeliveryMethod.COMMON
        }
    }

    "장바구니 상품 소유자 확인" {
        val cartProduct = CartProduct(
            memberId = 1L,
            productId = 1L,
            quantity = CartProductQuantity(5),
            sex = MALE,
            deliveryMethod = DeliveryMethod.COMMON,
            deliveryFee = 3000.toBigDecimal(),
        )

        shouldThrow<CartProductException> {
            cartProduct.validateOwner(2L)
        }
    }
})
