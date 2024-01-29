package com.petqua.domain.cart

enum class DeliveryMethod(
    val description: String,
) {
    
    COMMON("일반 운송"),
    SAFETY("안전 운송"),
    PICK_UP("직접 방문"),
}
