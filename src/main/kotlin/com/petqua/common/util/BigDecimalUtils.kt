package com.petqua.common.util

import java.math.BigDecimal

const val DEFAULT_SCALE = 2

fun BigDecimal.setDefaultScale(): BigDecimal {
    return this.setScale(DEFAULT_SCALE)
}
