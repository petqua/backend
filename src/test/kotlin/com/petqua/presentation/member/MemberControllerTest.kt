package com.petqua.presentation.member

import com.ninjasquad.springmockk.SpykBean
import com.petqua.domain.auth.AuthMemberRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.nickname.NicknameGenerator
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.presentation.member.dto.MemberSignUpRequest
import com.petqua.test.ApiTestConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.OK

class MemberControllerTest(
    private val authMemberRepository: AuthMemberRepository,
    private val nicknameWordRepository: NicknameWordRepository,
    private val memberRepository: MemberRepository,
    @SpykBean private val nicknameGenerator: NicknameGenerator,
) : ApiTestConfig() {

    init {

        Given("회원가입을 할 때") {
            nicknameWordRepository.saveAll(
                listOf(
                    NicknameWord(word = "펫쿠아"),
                    NicknameWord(word = "물고기"),
                )
            )
            val sighUpToken = requestLogin()

            When("임시 토큰으로 약관 동의 여부를 입력하면") {
                val response = requestSignUpBy(
                    signUpToken = sighUpToken,
                    MemberSignUpRequest(hasAgreedToMarketingNotification = true)
                )

                Then("200 OK로 응답한다") {
                    response.statusCode shouldBe OK.value()
                }

                Then("회원이 생성된다") {
                    val members = memberRepository.findAll()

                    members.size shouldBe 1
                    members[0].hasAgreedToMarketingNotification shouldBe true
                    members[0].nickname.value shouldContain "펫쿠아"
                    members[0].nickname.value shouldContain "물고기"
                }

                Then("액세스 토큰, 리프레시 토큰이 반환된다") {
                    val headers = response.headers()

                    headers.get(AUTHORIZATION) shouldNotBe null
                    headers.get(HttpHeaders.SET_COOKIE) shouldNotBe null
                }
            }
        }
    }
}
