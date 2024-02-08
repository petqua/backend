package com.petqua.common.query

data class QueryInfo(
    var count: Int = 0,
    var time: Long = 0L,
) {

    fun increaseCount() {
        count++
    }
}
