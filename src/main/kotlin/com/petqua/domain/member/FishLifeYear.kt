package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val FISH_LIFE_YEAR_MIN = 0
private const val FISH_LIFE_YEAR_MAX = 99
private const val EMPTY_YEAR = -1

@Embeddable
class FishLifeYear private constructor(
    @Column(nullable = false, name = "fish_life_year")
    val value: Int,
) {

    companion object {
        private val EMPTY_FISH_LIFE_YEAR = FishLifeYear(EMPTY_YEAR)

        fun emptyFishLifeYear(): FishLifeYear {
            return EMPTY_FISH_LIFE_YEAR
        }

        fun from(value: Int): FishLifeYear {
            throwExceptionWhen(value < FISH_LIFE_YEAR_MIN || value > FISH_LIFE_YEAR_MAX) {
                MemberException(INVALID_MEMBER_FISH_LIFE_YEAR)
            }
            return FishLifeYear(value)
        }
    }
}
