package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH_COUNT

class PetFishes(
    val values: List<PetFish>,
) {

    init {
        throwExceptionWhen(values.isEmpty()) {
            MemberException(INVALID_MEMBER_PET_FISH_COUNT)
        }
    }

    fun ids(): List<Long> {
        return values.map { it.fishId }
    }

    fun validateFishesByCount(countOfFishes: Int) {
        throwExceptionWhen(values.size != countOfFishes) {
            MemberException(INVALID_MEMBER_PET_FISH)
        }
    }
}
