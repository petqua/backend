package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH
import com.petqua.test.fixture.petFish
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PetFishesTest : StringSpec({

    "반려어 개수를 입력해 검증한다" {
        val fishIds = listOf(1L, 2L)

        val petFishes = PetFishes(
            listOf(
                petFish(fishId = fishIds[0]),
                petFish(fishId = fishIds[1])
            )
        )

        shouldNotThrow<MemberException> {
            petFishes.validateFishesByCount(fishIds.size)
        }
    }

    "반려어 개수를 입력해 검증할 때 개수가 서로 다르다면 예외를 던진다" {
        val fishIds = listOf(1L, 2L)

        val petFishes = PetFishes(
            listOf(
                petFish(fishId = fishIds[0]),
                petFish(fishId = fishIds[1])
            )
        )

        shouldThrow<MemberException> {
            petFishes.validateFishesByCount(fishIds.size + 1)
        }.exceptionType() shouldBe INVALID_MEMBER_PET_FISH
    }
})
