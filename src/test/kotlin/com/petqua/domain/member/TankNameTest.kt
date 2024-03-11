package com.petqua.domain.member

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_TANK_NAME
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TankNameTest : StringSpec({

    "숫자, 대소문자, 한글, 공백, 특정 특수문자를 포함해 생성할 수 있다" {
        val name = "nAm E이,/ _1"

        shouldNotThrow<MemberException> {
            TankName(name)
        }
    }

    "맨 앞에 공백이 있는 경우 생성할 수 없다" {
        val name = " name"

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }

    "맨 뒤에 공백이 있는 경우 생성할 수 없다" {
        val name = "name "

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }

    "연속된 공백이 있는 경우 생성할 수 없다" {
        val name = "na   me"

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }

    "2글자 미만인 경우 생성할 수 없다" {
        val name = "n"

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }

    "18글자 초과인 경우 생성할 수 없다" {
        val name = "name name name name name"

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }

    "허용하지 않은 특수문자를 포함할 경우 생성할 수 없다" {
        val name = "name !"

        shouldThrow<MemberException> {
            TankName(name)
        }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
    }
})
