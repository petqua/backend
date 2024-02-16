package com.petqua.domain.order

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class OrderNumber(

    @Column(nullable = false, unique = true)
    val value: String,
) {

    //TODO : 주문 번호 생성 로직
}
