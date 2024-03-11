package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH_COUNT
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val PET_FISH_COUNT_MIN = 1
private const val PET_FISH_COUNT_MAX = 99

@Embeddable
data class PetFishCount(
    @Column(nullable = false, name = "count")
    val value: Int,
) {
    init {
        throwExceptionWhen(value < PET_FISH_COUNT_MIN || value > PET_FISH_COUNT_MAX) {
            MemberException(INVALID_MEMBER_PET_FISH_COUNT)
        }
    }
}
