package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val FISH_LIFE_YEAR_MIN = 0
private const val FISH_LIFE_YEAR_MAX = 99

@Embeddable
data class FishLifeYear(
    @Column(nullable = false, name = "fish_life_year")
    val value: Int,
) {

    init {
        throwExceptionWhen(value < FISH_LIFE_YEAR_MIN || value > FISH_LIFE_YEAR_MAX) {
            MemberException(INVALID_MEMBER_FISH_LIFE_YEAR)
        }
    }
}
