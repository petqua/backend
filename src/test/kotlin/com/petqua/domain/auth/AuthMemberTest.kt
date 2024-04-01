package com.petqua.domain.auth

import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AuthMemberTest : StringSpec({

    "회원을 삭제한다" {
        val authCredentials = AuthCredentials(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            isDeleted = false,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        authCredentials.delete()

        assertSoftly(authCredentials) {
            it.isDeleted shouldBe true
            it.oauthId shouldBe -1L
            it.oauthAccessToken shouldBe ""
            it.oauthAccessTokenExpiresAt shouldBe null
            it.oauthRefreshToken shouldBe ""
        }
    }

    "회원 삭제 여부를 검증한다" {
        val authCredentials = AuthCredentials(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            isDeleted = false,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        shouldNotThrow<MemberException> {
            authCredentials.validateDeleted()
        }
    }

    "삭제된 회원에 대해 삭제 여부를 검증하면 예외를 던진다" {
        val authCredentials = AuthCredentials(
            id = 1L,
            oauthId = 1L,
            oauthServerNumber = OauthServerType.KAKAO.number,
            isDeleted = true,
            oauthAccessToken = "oauthAccessToken",
            oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(10000),
            oauthRefreshToken = "oauthRefreshToken",
        )

        shouldThrow<MemberException> {
            authCredentials.validateDeleted()
        }.exceptionType() shouldBe NOT_FOUND_MEMBER
    }

    "로그아웃 처리를 한다" {
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

        member.signOut()

        assertSoftly(member) {
            it.oauthAccessToken shouldBe ""
            it.oauthAccessTokenExpiresAt shouldBe null
            it.oauthRefreshToken shouldBe ""
        }
    }
})
