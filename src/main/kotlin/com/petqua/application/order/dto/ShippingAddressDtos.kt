package com.petqua.application.order.dto

import io.swagger.v3.oas.annotations.media.Schema

data class SaveShippingAddressCommand(
    @Schema(
        description = "멤버 id",
        example = "1"
    )
    val memberId: Long,

    @Schema(
        description = "배송지 이름",
        example = "집"
    )
    val name: String,

    @Schema(
        description = "받는 사람",
        example = "홍길동"
    )
    val receiver: String,

    @Schema(
        description = "전화 번호",
        example = "010-1234-1234"
    )
    val phoneNumber: String,

    @Schema(
        description = "우편 번호",
        example = "12345"
    )
    val zipCode: Int,

    @Schema(
        description = "주소",
        example = "서울특별시 강남구 역삼동 99번길"
    )
    val address: String,

    @Schema(
        description = "상세 주소",
        example = "101동 101호"
    )
    val detailAddress: String,

    @Schema(
        description = "기본 배송지 여부",
        example = "false"
    )
    val isDefaultAddress: Boolean = false,
)

data class SaveShippingAddressResponse(
    @Schema(
        description = "배송지 id",
        example = "1"
    )
    val id: Long,
)
