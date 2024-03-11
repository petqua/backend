package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FishLifeYearTest : StringSpec({

    "물생활 경력을 입력할 수 있다." {
        val year = 3

        shouldNotThrow<MemberException> {
            FishLifeYear(year)
        }
    }

    "물생활 경력이 0년 미만일 수 없다." {
        val year = -1

        shouldThrow<MemberException> {
            FishLifeYear(year)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_LIFE_YEAR
    }

    "물생활 경력이 99년을 초과할 수 없다." {
        val year = 100

        shouldThrow<MemberException> {
            FishLifeYear(year)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_LIFE_YEAR
    }
})
