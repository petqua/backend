package com.petqua.application.order.dto

import io.swagger.v3.oas.annotations.media.Schema

data class SaveShippingAddressCommand(
    val memberId: Long,
    val name: String,
    val receiver: String,
    val phoneNumber: String,
    val zipCode: Int,
    val address: String,
    val detailAddress: String,
    val isDefaultAddress: Boolean = false,
)

data class SaveShippingAddressResponse(
    @Schema(
        description = "배송지 id",
        example = "1"
    )
    val id: Long,
)
