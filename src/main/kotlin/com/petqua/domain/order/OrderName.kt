package com.petqua.domain.order

import com.petqua.domain.product.Product
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class OrderName(
    @Column(nullable = false, unique = true)
    val value: String,
) {

    companion object {
        fun from(products: List<Product>): OrderName {
            return OrderName("${products.first().name} 외 ${products.size - 1}건")
        }
    }
}
