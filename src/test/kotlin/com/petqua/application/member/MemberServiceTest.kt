package com.petqua.application.member

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.member.dto.MemberSignUpCommand
import com.petqua.domain.auth.AuthMemberRepository
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.member.FishTankRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.PetFishRepository
import com.petqua.domain.member.findByAuthMemberIdOrThrow
import com.petqua.domain.member.nickname.Nickname
import com.petqua.domain.member.nickname.NicknameGenerator
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.HAS_SIGNED_UP_MEMBER
import com.petqua.test.fixture.authMember
import com.petqua.test.fixture.member
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class MemberServiceTest(
    private val memberService: MemberService,
    private val authMemberRepository: AuthMemberRepository,
    private val nicknameWordRepository: NicknameWordRepository,
    private val memberRepository: MemberRepository,
    private val fishRepository: FishRepository,
    private val petFishRepository: PetFishRepository,
    private val fishTankRepository: FishTankRepository,

    @SpykBean private val nicknameGenerator: NicknameGenerator,
) : BehaviorSpec({

    Given("회원가입을 할 때") {
        nicknameWordRepository.saveAll(
            listOf(
                NicknameWord(word = "펫쿠아"),
                NicknameWord(word = "물고기"),
            )
        )
        val authMember = authMemberRepository.save(authMember())

        When("회원 id와 약관 동의 여부를 입력하면") {
            val authTokenInfo = memberService.signUp(
                MemberSignUpCommand(
                    authMemberId = authMember.id,
                    hasAgreedToMarketingNotification = true
                )
            )

            Then("동의 여부가 반영되고 회원의 닉네임이 생성된다") {
                val signedUpMember = memberRepository.findByAuthMemberIdOrThrow(authMember.id)

                signedUpMember.hasAgreedToMarketingNotification shouldBe true
                signedUpMember.nickname.value shouldContain "펫쿠아"
                signedUpMember.nickname.value shouldContain "물고기"
            }

            Then("회원의 액세스 토큰, 리프레시 토큰이 반환된다") {
                authTokenInfo.accessToken.isNotBlank() shouldBe true
                authTokenInfo.refreshToken.isNotBlank() shouldBe true
            }
        }

        When("이미 가입한 회원의 id 를 입력하면") {
            memberRepository.save(member(authMemberId = authMember.id))

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.signUp(
                        MemberSignUpCommand(
                            authMemberId = authMember.id,
                            hasAgreedToMarketingNotification = true
                        )
                    )
                }.exceptionType() shouldBe HAS_SIGNED_UP_MEMBER
            }
        }
    }

    Given("회원가입으로 회원의 닉네임을 생성할 때") {
        nicknameWordRepository.saveAll(
            listOf(
                NicknameWord(word = "펫쿠아"),
                NicknameWord(word = "물고기"),
            )
        )
        val authMember = authMemberRepository.save(authMember())

        every {
            nicknameGenerator.generate(nicknameWordRepository.findAll())
        } returns Nickname.from(
            "펫쿠아 물고기1"
        ) andThen Nickname.from(
            "펫쿠아 물고기2"
        )

        When("생성한 닉네임이 고유하다면") {
            memberService.signUp(
                MemberSignUpCommand(
                    authMemberId = authMember.id,
                    hasAgreedToMarketingNotification = true
                )
            )

            Then("닉네임을 한 번만 생성한다") {
                verify(exactly = 1) {
                    nicknameGenerator.generate(nicknameWordRepository.findAll())
                }
            }
        }

        When("생성한 닉네임이 고유하지 않다면") {
            memberRepository.save(member(nickname = "펫쿠아 물고기1"))

            memberService.signUp(
                MemberSignUpCommand(
                    authMemberId = authMember.id,
                    hasAgreedToMarketingNotification = true
                )
            )

            Then("닉네임을 여러 번 생성한다") {
                verify(atLeast = 2) {
                    nicknameGenerator.generate(nicknameWordRepository.findAll())
                }
            }
        }
    }

    /*Given("회원 정보를 입력할 때") {
        val fish = fishRepository.save(fish())

        val authMember = authMemberRepository.save(authMember())

        When("회원 정보를 모두 입력하면") {
            memberService.addProfile(
                MemberAddProfileCommand(
                    memberId = authMember.id,
                    fishTankName = "펫쿠아 어항",
                    installationDate = YearMonth.of(2024, 3),
                    fishTankSize = "TANK_1",
                    fishLifeYear = 1,
                    petFishes = listOf(
                        PetFishAddCommand(
                            fishId = fish.id,
                            sex = "FEMALE",
                            count = 1
                        )
                    )
                )
            )

            Then("입력한 정보가 반영된다") {
                memberRepository.findByIdOrThrow(authMember.id)
            }
        }
    }*/
})
