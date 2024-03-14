package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH

class PetFishes(
    val values: List<PetFish>,
) {

    fun ids(): List<Long> {
        return values.map { it.fishId }
    }

    fun validateFishes(fishesCount: Int) {
        throwExceptionWhen(values.size != fishesCount) {
            MemberException(INVALID_MEMBER_PET_FISH)
        }
    }
}
