package com.petqua.application.member

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.member.dto.MemberAddProfileCommand
import com.petqua.application.member.dto.MemberSignUpCommand
import com.petqua.application.member.dto.PetFishAddCommand
import com.petqua.application.member.dto.UpdateProfileCommand
import com.petqua.domain.auth.AuthCredentialsRepository
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.member.FishTankRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.PetFishRepository
import com.petqua.domain.member.PetFishSex.FEMALE
import com.petqua.domain.member.PetFishSex.MALE
import com.petqua.domain.member.TankSize.TANK_1
import com.petqua.domain.member.findByAuthCredentialsIdOrThrow
import com.petqua.domain.member.nickname.Nickname
import com.petqua.domain.member.nickname.NicknameGenerator
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.domain.policy.bannedword.BannedWord
import com.petqua.domain.policy.bannedword.BannedWordRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.ALREADY_EXIST_NICKNAME
import com.petqua.exception.member.MemberExceptionType.CONTAINING_BANNED_WORD_NAME
import com.petqua.exception.member.MemberExceptionType.FAILED_NICKNAME_GENERATION
import com.petqua.exception.member.MemberExceptionType.HAS_SIGNED_UP_MEMBER
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_SEX
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_TANK_NAME
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_FISH_TANK_SIZE
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_PET_FISH_COUNT
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.authCredentials
import com.petqua.test.fixture.fish
import com.petqua.test.fixture.member
import com.petqua.test.fixture.memberAddProfileCommand
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import java.time.LocalDate
import java.time.YearMonth

@SpringBootTest(webEnvironment = NONE)
class MemberServiceTest(
    private val memberService: MemberService,
    private val authCredentialsRepository: AuthCredentialsRepository,
    private val nicknameWordRepository: NicknameWordRepository,
    private val memberRepository: MemberRepository,
    private val bannedWordRepository: BannedWordRepository,
    private val fishRepository: FishRepository,
    private val petFishRepository: PetFishRepository,
    private val fishTankRepository: FishTankRepository,
    private val dataCleaner: DataCleaner,

    @SpykBean private val nicknameGenerator: NicknameGenerator,
) : BehaviorSpec({

    Given("회원가입을 할 때") {
        nicknameWordRepository.saveAll(
            listOf(
                NicknameWord(word = "펫쿠아"),
                NicknameWord(word = "물고기"),
            )
        )
        val authCredentials = authCredentialsRepository.save(authCredentials())

        When("회원 id와 약관 동의 여부를 입력하면") {
            val authTokenInfo = memberService.signUp(
                MemberSignUpCommand(
                    authCredentialsId = authCredentials.id,
                    hasAgreedToMarketingNotification = true
                )
            )

            Then("동의 여부가 반영되고 회원의 닉네임이 생성된다") {
                val signedUpMember = memberRepository.findByAuthCredentialsIdOrThrow(authCredentials.id)

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
            memberRepository.save(member(authCredentialsId = authCredentials.id))

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.signUp(
                        MemberSignUpCommand(
                            authCredentialsId = authCredentials.id,
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
        val authCredentials = authCredentialsRepository.save(authCredentials())

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
                    authCredentialsId = authCredentials.id,
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
                    authCredentialsId = authCredentials.id,
                    hasAgreedToMarketingNotification = true
                )
            )

            Then("닉네임을 여러 번 생성한다") {
                verify(atLeast = 2) {
                    nicknameGenerator.generate(nicknameWordRepository.findAll())
                }
            }
        }

        When("고유한 닉네임을 생성할 때 일정 횟수 이상 초과해서 생성하면") {
            memberRepository.save(member(nickname = "펫쿠아 물고기1"))
            memberRepository.save(member(nickname = "펫쿠아 물고기2"))

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.signUp(
                        MemberSignUpCommand(
                            authCredentialsId = authCredentials.id,
                            hasAgreedToMarketingNotification = true
                        )
                    )
                }.exceptionType() shouldBe FAILED_NICKNAME_GENERATION

                verify(atLeast = 10) {
                    nicknameGenerator.generate(nicknameWordRepository.findAll())
                }
            }
        }
    }

    Given("이름에 금지 단어가 포함되는지 여부를 검증할 때") {
        bannedWordRepository.saveAll(
            listOf(
                BannedWord(word = "금지"),
                BannedWord(word = "단어")
            )
        )

        When("금지 단어가 포함된 이름을 입력하면") {
            val name = "금지 단어 포함 이름"

            Then("예외를 던진다") {

                shouldThrow<MemberException> {
                    memberService.validateContainingBannedWord(name)
                }.exceptionType() shouldBe CONTAINING_BANNED_WORD_NAME
            }
        }

        When("금지 단어가 포함되지 않은 이름을 입력하면") {
            val name = "포함되지 않은 이름"

            Then("예외를 던지지 않는다") {

                shouldNotThrow<MemberException> {
                    memberService.validateContainingBannedWord(name)
                }
            }
        }
    }

    Given("회원 물생활 프로필을 등록할 때") {
        bannedWordRepository.saveAll(
            listOf(
                BannedWord(word = "금지"),
                BannedWord(word = "단어")
            )
        )
        val fish = fishRepository.save(fish())

        val authCredentials = authCredentialsRepository.save(authCredentials())
        val member = memberRepository.save(member(authCredentialsId = authCredentials.id))

        When("어항과 수조, 반려어 등 정보를 입력하면") {
            memberService.addProfile(
                MemberAddProfileCommand(
                    memberId = member.id,
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

            Then("입력한 회원의 정보가 저장된다") {
                assertSoftly {
                    member.fishTankCount shouldBe 1
                    member.fishLifeYear.value shouldBe 1
                }
            }

            Then("입력한 회원의 수조 정보가 저장된다") {
                val fishTanks = fishTankRepository.findByMemberId(member.id)

                assertSoftly {
                    fishTanks.size shouldBe 1

                    val fishTank = fishTanks[0]
                    fishTank.memberId shouldBe member.id
                    fishTank.name.value shouldBe "펫쿠아 어항"
                    fishTank.installationDate shouldBe LocalDate.of(2024, 3, 1)
                    fishTank.size shouldBe TANK_1
                }
            }

            Then("입력한 회원의 반려어 정보가 저장된다") {
                val fishTank = fishTankRepository.findByMemberId(member.id)[0]
                val petFishes = petFishRepository.findByFishTankId(fishTank.id)

                assertSoftly {
                    petFishes.size shouldBe 1

                    val petFish = petFishes[0]
                    petFish.memberId shouldBe member.id
                    petFish.fishId shouldBe fish.id
                    petFish.fishTankId shouldBe fishTank.id
                    petFish.sex shouldBe FEMALE
                    petFish.count.value shouldBe 1
                }
            }
        }

        When("같은 반려어를 서로 다른 성별로 정보를 입력하면") {
            memberService.addProfile(
                MemberAddProfileCommand(
                    memberId = member.id,
                    fishTankName = "펫쿠아 어항",
                    installationDate = YearMonth.of(2024, 3),
                    fishTankSize = "TANK_1",
                    fishLifeYear = 1,
                    petFishes = listOf(
                        PetFishAddCommand(
                            fishId = fish.id,
                            sex = "FEMALE",
                            count = 1
                        ),
                        PetFishAddCommand(
                            fishId = fish.id,
                            sex = "MALE",
                            count = 1
                        )
                    )
                )
            )

            Then("입력한 회원의 반려어 정보가 저장된다") {
                val fishTank = fishTankRepository.findByMemberId(member.id)[0]
                val petFishes = petFishRepository.findByFishTankId(fishTank.id)

                assertSoftly {
                    petFishes.size shouldBe 2

                    val femalePetFish = petFishes[0]
                    femalePetFish.memberId shouldBe member.id
                    femalePetFish.fishId shouldBe fish.id
                    femalePetFish.fishTankId shouldBe fishTank.id
                    femalePetFish.sex shouldBe FEMALE
                    femalePetFish.count.value shouldBe 1

                    val malePetFish = petFishes[1]
                    malePetFish.memberId shouldBe member.id
                    malePetFish.fishId shouldBe fish.id
                    malePetFish.fishTankId shouldBe fishTank.id
                    malePetFish.sex shouldBe MALE
                    malePetFish.count.value shouldBe 1
                }
            }
        }

        When("수조 이름으로 부적절한 단어를 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                fishTankName = "금지단어포함이름"
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe CONTAINING_BANNED_WORD_NAME
            }
        }

        When("존재하지 않는 회원의 id 를 입력하면") {
            val command = memberAddProfileCommand(memberId = Long.MIN_VALUE)

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }

        When("유효하지 않은 물생활 경력을 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                fishLifeYear = -1
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_FISH_LIFE_YEAR
            }
        }


        When("수조 이름 정책을 위반해서 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                fishTankName = "열 여 덟 자 이 상 으 로 긴 어 항 이 름"
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_NAME
            }
        }

        When("존재하지 않는 수조 크기를 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                fishTankSize = "TANK_100"
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_FISH_TANK_SIZE
            }
        }

        When("존재하지 않는 반려어 id를 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                petFishes = listOf(
                    PetFishAddCommand(
                        fishId = Long.MIN_VALUE,
                        sex = "FEMALE",
                        count = 1
                    )
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_PET_FISH
            }
        }

        When("존재하지 않는 반려어 성별을 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                petFishes = listOf(
                    PetFishAddCommand(
                        fishId = fish.id,
                        sex = "수컷",
                        count = 1
                    )
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_FISH_SEX
            }
        }

        When("유효하지 않은 반려어 마릿수를 입력하면") {
            val command = memberAddProfileCommand(
                memberId = member.id,
                petFishes = listOf(
                    PetFishAddCommand(
                        fishId = fish.id,
                        sex = "FEMALE",
                        count = -1
                    )
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    memberService.addProfile(command)
                }.exceptionType() shouldBe INVALID_MEMBER_PET_FISH_COUNT
            }
        }
    }

    Given("프로필 수정을 할 때") {
        val member = memberRepository.save(member(nickname = "닉네임"))
        bannedWordRepository.save(BannedWord(word = "금지 단어"))

        When("닉네임을 입력하면") {
            val command = UpdateProfileCommand(
                memberId = member.id,
                nickname = "변경된 닉네임",
            )
            memberService.updateProfile(command)

            Then("닉네임을 수정한다") {
                val updatedMember = memberRepository.findById(member.id).get()

                assertSoftly {
                    updatedMember.nickname.value shouldBe "변경된 닉네임"
                }
            }
        }

        When("닉네임에 부적절한 단어가 들어가면") {
            val command = UpdateProfileCommand(
                memberId = member.id,
                nickname = "금지 단어",
            )

            Then("예외를 던진다") {
                shouldThrow<MemberException> {
                    memberService.updateProfile(command)
                }.exceptionType() shouldBe CONTAINING_BANNED_WORD_NAME
            }
        }

        When("이미 다른 회원이 사용중인 닉네임이라면") {
            memberRepository.save(member(nickname = "변경된 닉네임"))
            val command = UpdateProfileCommand(
                memberId = member.id,
                nickname = "변경된 닉네임",
            )

            Then("예외를 던진다") {
                shouldThrow<MemberException> {
                    memberService.updateProfile(command)
                }.exceptionType() shouldBe ALREADY_EXIST_NICKNAME
            }
        }

        When("회원이 존재하지 않으면") {
            val command = UpdateProfileCommand(
                memberId = Long.MAX_VALUE,
                nickname = "변경된 닉네임",
            )

            Then("예외를 던진다") {
                shouldThrow<MemberException> {
                    memberService.updateProfile(command)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
