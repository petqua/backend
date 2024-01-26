package com.petqua.test.fixture

import com.petqua.domain.store.Store

fun store(
    id: Long = 0L,
    name: String = "store",
): Store {
    return Store(id, name)
}
