package com.petqua.test.fixture

import com.petqua.domain.fish.Fish
import com.petqua.domain.fish.Species

fun fish(
    id: Long = 0L,
    species: String = "species",
): Fish {
    return Fish(
        id = id,
        species = Species.from(species)
    )
}
