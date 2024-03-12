package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val FISH_LIFE_YEAR_MIN = 0
private const val FISH_LIFE_YEAR_MAX = 99
private const val FISH_LIFE_YEAR_FOR_ANONYMOUS = -1

@Embeddable
class FishLifeYear private constructor(
    @Column(nullable = false, name = "fish_life_year")
    val value: Int,
) {

    companion object {
        private val FOR_ANONYMOUS = FishLifeYear(FISH_LIFE_YEAR_FOR_ANONYMOUS)

        fun forAnonymous(): FishLifeYear {
            return FOR_ANONYMOUS
        }

        fun from(value: Int): FishLifeYear {
            throwExceptionWhen(value < FISH_LIFE_YEAR_MIN || value > FISH_LIFE_YEAR_MAX) {
                MemberException(INVALID_MEMBER_FISH_LIFE_YEAR)
            }
            return FishLifeYear(value)
        }
    }
}
