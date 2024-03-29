package com.petqua.domain.member

import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class MemberTest : StringSpec({

    "회원을 삭제한다" {
        val member = Member(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            authority = Authority.MEMBER,
            isDeleted = false,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        member.delete()

        assertSoftly(member) {
            it.isDeleted shouldBe true
            it.nickname shouldBe ""
        }
    }

    "회원 삭제 여부를 검증한다" {
        val member = Member(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            authority = Authority.MEMBER,
            isDeleted = false,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        shouldNotThrow<MemberException> {
            member.validateDeleted()
        }
    }

    "삭제된 회원에 대해 삭제 여부를 검증하면 예외를 던진다" {
        val member = Member(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            authority = Authority.MEMBER,
            isDeleted = true,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        shouldThrow<MemberException> {
            member.validateDeleted()
        }.exceptionType() shouldBe NOT_FOUND_MEMBER
    }
})
