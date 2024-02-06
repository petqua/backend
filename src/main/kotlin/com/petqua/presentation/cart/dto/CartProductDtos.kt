package com.petqua.presentation.cart.dto

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.cart.DeliveryMethod
import io.swagger.v3.oas.annotations.media.Schema

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
        description = "수컷 여부",
        example = "true",
        allowableValues = ["true", "false"]
    )
    val isMale: Boolean,

    @Schema(
        description = "배송 방법(\"COMMON : 일반\", \"SAFETY : 안전\", \"PICK_UP : 직접\")",
        example = "COMMON",
        allowableValues = ["COMMON", "SAFETY", "PICK_UP"]
    )
    val deliveryMethod: String,
) {

    fun toCommand(memberId: Long): SaveCartProductCommand {
        return SaveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = quantity,
            isMale = isMale,
            deliveryMethod = DeliveryMethod.from(deliveryMethod)
        )
    }
}

data class UpdateCartProductOptionRequest(
    @Schema(
        description = "상품 개수",
        example = "1"
    )
    val quantity: Int,

    @Schema(
        description = "수컷 여부",
        example = "true",
        allowableValues = ["true", "false"]
    )
    val isMale: Boolean,

    @Schema(
        description = "배송 방법(\"COMMON : 일반\", \"SAFETY : 안전\", \"PICK_UP : 직접\")",
        example = "COMMON",
        allowableValues = ["COMMON", "SAFETY", "PICK_UP"]
    )
    val deliveryMethod: String,
) {

    fun toCommand(memberId: Long, cartProductId: Long): UpdateCartProductOptionCommand {
        return UpdateCartProductOptionCommand(
            memberId = memberId,
            cartProductId = cartProductId,
            quantity = CartProductQuantity(quantity),
            isMale = isMale,
            deliveryMethod = DeliveryMethod.from(deliveryMethod)
        )
    }
}
