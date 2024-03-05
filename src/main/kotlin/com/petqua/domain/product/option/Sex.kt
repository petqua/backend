package com.petqua.domain.product.option

import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_PRODUCT_OPTION
import java.util.Locale

enum class Sex(
    val description: String,
) {

    FEMALE("암"),
    MALE("수"),
    HERMAPHRODITE("자웅동체"),
    ;

    companion object {
        fun from(name: String): Sex {
            return enumValues<Sex>().find { it.name == name.uppercase(Locale.ENGLISH) }
                ?: throw ProductException(INVALID_PRODUCT_OPTION)
        }
    }
}
