package com.petqua.test.fixture

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.common.util.setDefaultScale
import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.presentation.cart.dto.SaveCartProductRequest
import com.petqua.presentation.cart.dto.UpdateCartProductOptionRequest
import java.math.BigDecimal

fun cartProduct(
    id: Long = 0L,
    memberId: Long = 1L,
    productId: Long = 1L,
    quantity: Int = 1,
    sex: Sex = MALE,
    deliveryMethod: DeliveryMethod = COMMON,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
): CartProduct {
    return CartProduct(
        id = id,
        memberId = memberId,
        productId = productId,
        quantity = CartProductQuantity(quantity),
        sex = sex,
        deliveryMethod = deliveryMethod,
        deliveryFee = deliveryFee,
    )
}

// applications
fun saveCartProductCommand(
    memberId: Long = 0L,
    productId: Long = 0L,
    quantity: Int = 1,
    sex: Sex = MALE,
    deliveryMethod: DeliveryMethod = COMMON,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
): SaveCartProductCommand {
    return SaveCartProductCommand(
        memberId = memberId,
        productId = productId,
        quantity = quantity,
        sex = sex,
        deliveryMethod = deliveryMethod,
        deliveryFee = deliveryFee.setDefaultScale(),
    )
}

fun updateCartProductOptionCommand(
    memberId: Long = 0L,
    cartProductId: Long = 0L,
    quantity: Int = 1,
    sex: Sex = MALE,
    deliveryMethod: DeliveryMethod = COMMON,
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
): UpdateCartProductOptionCommand {
    return UpdateCartProductOptionCommand(
        memberId = memberId,
        cartProductId = cartProductId,
        quantity = CartProductQuantity(quantity),
        sex = sex,
        deliveryMethod = deliveryMethod,
        deliveryFee = deliveryFee.setDefaultScale(),
    )
}

// controller
fun saveCartProductRequest(
    productId: Long = 0L,
    quantity: Int = 1,
    sex: Sex = MALE,
    deliveryMethod: String = "COMMON",
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
): SaveCartProductRequest {
    return SaveCartProductRequest(
        productId = productId,
        quantity = quantity,
        sex = sex,
        deliveryMethod = deliveryMethod,
        deliveryFee = deliveryFee.setDefaultScale(),
    )
}

fun updateCartProductOptionRequest(
    quantity: Int = 1,
    sex: Sex = MALE,
    deliveryMethod: String = "COMMON",
    deliveryFee: BigDecimal = 3000.toBigDecimal(),
): UpdateCartProductOptionRequest {
    return UpdateCartProductOptionRequest(
        quantity = quantity,
        sex = sex,
        deliveryMethod = deliveryMethod,
        deliveryFee = deliveryFee.setDefaultScale(),
    )
}

