package com.petqua.domain.auth.token

import com.petqua.domain.member.MemberRepository
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.Date
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class BlackListTokenCacheStorageTest(
    private val blackListTokenCacheStorage: BlackListTokenCacheStorage,
    private val authTokenProvider: AuthTokenProvider,
    private val memberRepository: MemberRepository,
) : StringSpec({

    "블랙리스트 추가 테스트" {
        val member = memberRepository.save(member())
        val authTokens = authTokenProvider.createAuthToken(member, Date())

        blackListTokenCacheStorage.save(member.id, authTokens.accessToken)

        assertSoftly {
            blackListTokenCacheStorage.isBlackListed(member.id, authTokens.accessToken) shouldBe true
        }
    }
})
