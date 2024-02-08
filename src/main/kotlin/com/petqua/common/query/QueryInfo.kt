package com.petqua.common.query

data class QueryInfo(
    var count: Int = 0,
) {

    fun increaseCount() {
        count++
    }
}
