package com.petqua.domain.fish

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.fish.FishException
import com.petqua.exception.fish.FishExceptionType.INVALID_SPECIES_NAME
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Species(
    @Column(nullable = false, name = "species")
    val name: String,
) {
    companion object {
        fun from(name: String): Species {
            validateSpeciesName(name)
            return Species(name)
        }

        private fun validateSpeciesName(name: String) {
            throwExceptionWhen(name.isBlank()) {
                FishException(INVALID_SPECIES_NAME)
            }
        }
    }
}
