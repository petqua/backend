package com.petqua.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class OrderName(

    @Column(nullable = false, unique = true)
    val value: String,
) {

    //TODO : 주문 이름 생성 로직
}
