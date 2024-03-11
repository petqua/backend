package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH_COUNT
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PetFishCountTest : StringSpec({

    "반려어 수를 입력할 수 있다." {
        val count = 3

        shouldNotThrow<MemberException> {
            PetFishCount(count)
        }
    }

    "반려어 수가 1마리 미만일 수 없다." {
        val count = 0

        shouldThrow<MemberException> {
            PetFishCount(count)
        }.exceptionType() shouldBe INVALID_MEMBER_PET_FISH_COUNT
    }

    "반려어 수가 99마리를 초과할 수 없다." {
        val count = 100

        shouldThrow<MemberException> {
            PetFishCount(count)
        }.exceptionType() shouldBe INVALID_MEMBER_PET_FISH_COUNT
    }
})
