package com.petqua.test.fixture

import com.petqua.domain.order.ShippingAddress

fun shippingAddress(
    id: Long = 0L,
    memberId: Long = 0L,
    name: String = "집",
    receiver: String = "홍길동",
    phoneNumber: String = "010-1234-5678",
    zipCode: Int = 12345,
    address: String = "서울시 강남구 역삼동 99번길",
    detailAddress: String = "101동 101호",
    isDefaultAddress: Boolean = false,
): ShippingAddress {
    return ShippingAddress(
        id = id,
        memberId = memberId,
        name = name,
        receiver = receiver,
        phoneNumber = phoneNumber,
        zipCode = zipCode,
        address = address,
        detailAddress = detailAddress,
        isDefaultAddress = isDefaultAddress,
    )
}
