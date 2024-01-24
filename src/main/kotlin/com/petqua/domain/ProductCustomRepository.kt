package com.petqua.domain

import com.petqua.application.ProductReadConditions

interface ProductCustomRepository {

    fun findAllByConditions(conditions: ProductReadConditions): List<Product>
}
