package com.petqua.domain.member.nickname

import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_NICKNAME
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NicknameTest : StringSpec({

    "숫자, 대소문자, 한글, 공백을 포함해 생성할 수 있다" {
        val nickname = "nAm E이1"

        shouldNotThrow<MemberException> {
            Nickname.from(nickname)
        }
    }

    "맨 앞에 공백이 있는 경우 생성할 수 없다" {
        val nickname = " nickname"

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }

    "맨 뒤에 공백이 있는 경우 생성할 수 없다" {
        val nickname = "nickname "

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }

    "연속된 공백이 있는 경우 생성할 수 없다" {
        val nickname = "na   me"

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }

    "2글자 미만인 경우 생성할 수 없다" {
        val nickname = "n"

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }

    "12글자 초과인 경우 생성할 수 없다" {
        val nickname = "nickname nickname nickname nickname"

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }

    "허용하지 않은 특수문자를 포함할 경우 생성할 수 없다" {
        val nickname = "nickname_"

        shouldThrow<MemberException> {
            Nickname.from(nickname)
        }.exceptionType() shouldBe INVALID_MEMBER_NICKNAME
    }
})
