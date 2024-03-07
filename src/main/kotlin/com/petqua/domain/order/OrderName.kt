package com.petqua.domain.order

import com.petqua.domain.product.ProductSnapshot
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class OrderName(
    @Column(nullable = false, unique = true)
    val value: String,
) {

    companion object {
        fun from(productSnapshots: List<ProductSnapshot>): OrderName {
            return OrderName("${productSnapshots.first().name} 외 ${productSnapshots.size - 1}건")
        }
    }
}
