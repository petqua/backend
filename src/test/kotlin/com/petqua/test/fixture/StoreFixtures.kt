package com.petqua.test.fixture

import com.petqua.domain.Store

fun store(
    id: Long = 0L,
    name: String = "store",
): Store {
    return Store(id, name)
}
