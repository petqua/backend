package com.petqua.presentation.cart.dto

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.option.Sex
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class SaveCartProductRequest(
    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상품 개수",
        example = "1"
    )
    val quantity: Int,

    @Schema(
        description = "성별",
        example = "MALE",
        allowableValues = ["MALE", "FEMALE", "HERMAPHRODITE"]
    )
    val sex: Sex,

    @Schema(
        description = "배송 방법(\"COMMON : 일반\", \"SAFETY : 안전\", \"PICK_UP : 직접\")",
        example = "COMMON",
        allowableValues = ["COMMON", "SAFETY", "PICK_UP"]
    )
    val deliveryMethod: String,

    @Schema(
        description = "배송비",
        example = "3000"
    )
    val deliveryFee: BigDecimal,
) {

    fun toCommand(memberId: Long): SaveCartProductCommand {
        return SaveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = quantity,
            sex = sex,
            deliveryMethod = DeliveryMethod.from(deliveryMethod),
            deliveryFee = deliveryFee,
        )
    }
}

data class UpdateCartProductOptionRequest(
    @Schema(
        description = "봉달(장바구니) 수량",
        example = "1"
    )
    val quantity: Int,

    @Schema(
        description = "성별",
        example = "MALE",
        allowableValues = ["MALE", "FEMALE", "HERMAPHRODITE"]
    )
    val sex: Sex,

    @Schema(
        description = "배송 방법(\"COMMON : 일반\", \"SAFETY : 안전\", \"PICK_UP : 직접\")",
        example = "COMMON",
        allowableValues = ["COMMON", "SAFETY", "PICK_UP"]
    )
    val deliveryMethod: String,

    @Schema(
        description = "배송비",
        example = "3000"
    )
    val deliveryFee: BigDecimal,
) {

    fun toCommand(memberId: Long, cartProductId: Long): UpdateCartProductOptionCommand {
        return UpdateCartProductOptionCommand(
            memberId = memberId,
            cartProductId = cartProductId,
            quantity = CartProductQuantity(quantity),
            sex = sex,
            deliveryMethod = DeliveryMethod.from(deliveryMethod),
            deliveryFee = deliveryFee,
        )
    }
}
