package com.petqua.presentation.member

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.member.FishTankRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.PetFishRepository
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.domain.policy.bannedword.BannedWord
import com.petqua.domain.policy.bannedword.BannedWordRepository
import com.petqua.exception.member.MemberExceptionType
import com.petqua.exception.member.MemberExceptionType.CONTAINING_BANNED_WORD_NAME
import com.petqua.exception.member.MemberExceptionType.HAS_SIGNED_UP_MEMBER
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.presentation.member.dto.MemberSignUpRequest
import com.petqua.presentation.member.dto.UpdateProfileRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import java.util.*

class MemberControllerTest(
    private val nicknameWordRepository: NicknameWordRepository,
    private val memberRepository: MemberRepository,
    private val bannedWordRepository: BannedWordRepository,
    private val fishRepository: FishRepository,
    private val petFishRepository: PetFishRepository,
    private val fishTankRepository: FishTankRepository,
    private val authTokenProvider: AuthTokenProvider,
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
                val response = requestSignUp(
                    signUpToken = sighUpToken,
                    MemberSignUpRequest(hasAgreedToMarketingNotification = true)
                )

                Then("201 CREATED 로 응답한다") {
                    response.statusCode shouldBe CREATED.value()
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

            When("이미 가입한 회원이 회원가입을 요청하면") {
                requestSignUp(
                    signUpToken = sighUpToken,
                    MemberSignUpRequest(hasAgreedToMarketingNotification = true)
                )

                val response = requestSignUp(
                    signUpToken = sighUpToken,
                    MemberSignUpRequest(hasAgreedToMarketingNotification = true)
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe HAS_SIGNED_UP_MEMBER.errorMessage()
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
                val response = requestValidateContainingBannedWord(name)

                Then("예외를 던진다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe CONTAINING_BANNED_WORD_NAME.errorMessage()
                    }
                }
            }

            When("금지 단어가 포함되지 않은 이름을 입력하면") {
                val name = "포함되지 않은 이름"
                val response = requestValidateContainingBannedWord(name)

                Then("예외를 던지지 않는다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }
//
//        Given("회원 물생활 프로필을 등록할 때") {
//            nicknameWordRepository.saveAll(
//                listOf(
//                    NicknameWord(word = "펫쿠아"),
//                    NicknameWord(word = "물고기"),
//                )
//            )
//            bannedWordRepository.saveAll(
//                listOf(
//                    BannedWord(word = "금지"),
//                    BannedWord(word = "단어")
//                )
//            )
//            val fish = fishRepository.save(fish())
//
//            val accessToken = signInAsMember().accessToken
//            val memberId = getMemberIdByAccessToken(accessToken)
//
//            When("어항과 수조, 반려어 등 정보를 입력하면") {
//                val request = MemberAddProfileRequest(
//                    fishTankName = "펫쿠아 어항",
//                    installationDate = YearMonth.of(2024, 3),
//                    fishTankSize = "TANK_1",
//                    fishLifeYear = 1,
//                    petFishes = listOf(
//                        PetFishAddRequest(
//                            fishId = fish.id,
//                            sex = "FEMALE",
//                            count = 1
//                        )
//                    )
//                )
//
//                val response = requestAddProfile(request, accessToken)
//
//                Then("회원의 물생활 정보가 저장된다") {
//                    assertSoftly {
//                        response.statusCode shouldBe NO_CONTENT.value()
//
//                        val member = memberRepository.findByIdOrThrow(memberId)
//                        member.fishTankCount shouldBe 1
//                        member.fishLifeYear.value shouldBe 1
//
//                        val fishTank = fishTankRepository.findByMemberId(member.id)[0]
//                        fishTank.memberId shouldBe member.id
//                        fishTank.name.value shouldBe "펫쿠아 어항"
//                        fishTank.installationDate shouldBe LocalDate.of(2024, 3, 1)
//                        fishTank.size shouldBe TankSize.TANK_1
//
//                        val petFish = petFishRepository.findByFishTankId(fishTank.id)[0]
//                        petFish.memberId shouldBe member.id
//                        petFish.fishId shouldBe fish.id
//                        petFish.fishTankId shouldBe fishTank.id
//                        petFish.sex shouldBe PetFishSex.FEMALE
//                        petFish.count.value shouldBe 1
//                    }
//                }
//            }
//
//            When("수조 이름으로 부적절한 단어를 입력하면") {
//                val request = memberAddProfileRequest(fishTankName = "금지단어포함이름")
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe CONTAINING_BANNED_WORD_NAME.errorMessage()
//                    }
//                }
//            }
//
//            When("유효하지 않은 물생활 경력을 입력하면") {
//                val request = memberAddProfileRequest(fishLifeYear = -1)
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_FISH_LIFE_YEAR.errorMessage()
//                    }
//                }
//            }
//
//            When("수조 이름 정책을 위반해서 입력하면") {
//                val request = memberAddProfileRequest(fishTankName = "열 여 덟 자 이 상 으 로 긴 어 항 이 름")
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_FISH_TANK_NAME.errorMessage()
//                    }
//                }
//            }
//
//            When("존재하지 않는 수조 크기를 입력하면") {
//                val request = memberAddProfileRequest(fishTankSize = "TANK_100")
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_FISH_TANK_SIZE.errorMessage()
//                    }
//                }
//            }
//
//            When("존재하지 않는 반려어 id를 입력하면") {
//                val request = memberAddProfileRequest(
//                    petFishes = listOf(
//                        PetFishAddRequest(
//                            fishId = MIN_VALUE,
//                            sex = "FEMALE",
//                            count = 1
//                        )
//                    )
//                )
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_PET_FISH.errorMessage()
//                    }
//                }
//            }
//
//            When("존재하지 않는 반려어 성별을 입력하면") {
//                val request = memberAddProfileRequest(
//                    petFishes = listOf(
//                        PetFishAddRequest(
//                            fishId = MIN_VALUE,
//                            sex = "수컷",
//                            count = 1
//                        )
//                    )
//                )
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_FISH_SEX.errorMessage()
//                    }
//                }
//            }
//
//            When("유효하지 않은 반려어 마릿수를 입력하면") {
//                val request = memberAddProfileRequest(
//                    petFishes = listOf(
//                        PetFishAddRequest(
//                            fishId = MIN_VALUE,
//                            sex = "FEMALE",
//                            count = -1
//                        )
//                    )
//                )
//                val response = requestAddProfile(request, accessToken)
//
//                Then("예외를 응답한다") {
//                    val errorResponse = response.`as`(ExceptionResponse::class.java)
//                    assertSoftly(response) {
//                        statusCode shouldBe BAD_REQUEST.value()
//                        errorResponse.message shouldBe MemberExceptionType.INVALID_MEMBER_PET_FISH_COUNT.errorMessage()
//                    }
//                }
//            }
//        }

        Given("회원 프로필을 수정할 때") {
            val accessToken = signInAsMember().accessToken
            bannedWordRepository.saveAll(
                listOf(
                    BannedWord(word = "금지 단어"),
                )
            )

            When("닉네임을 입력하면") {
                val request = UpdateProfileRequest(nickname = "변경된 닉네임")
                val response = requestUpdateProfile(request, accessToken)

                Then("회원의 닉네임을 수정한다") {
                    assertSoftly {
                        response.statusCode shouldBe NO_CONTENT.value()

                        val updatedMember = memberRepository.findByIdOrThrow(1L)
                        updatedMember.nickname.value shouldBe "변경된 닉네임"
                    }
                }
            }

            When("닉네임에 부적절한 단어가 들어가면") {
                val request = UpdateProfileRequest(nickname = "금지 단어")
                val response = requestUpdateProfile(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe CONTAINING_BANNED_WORD_NAME.errorMessage()
                    }
                }
            }

            When("이미 다른 회원이 사용중인 닉네임이라면") {
                memberRepository.save(member(nickname = "변경된 닉네임"))
                val request = UpdateProfileRequest(nickname = "변경된 닉네임")
                val response = requestUpdateProfile(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe MemberExceptionType.ALREADY_EXIST_NICKNAME.errorMessage()
                    }
                }
            }

            When("회원이 존재하지 않으면") {
                val request = UpdateProfileRequest(nickname = "변경된 닉네임")
                val accessTokenOfNoExistMember = authTokenProvider.createLoginAuthToken(
                    memberId = Long.MAX_VALUE,
                    authority = Authority.MEMBER,
                    issuedDate = Date()
                ).getAccessToken()
                val response = requestUpdateProfile(request, accessTokenOfNoExistMember)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe NOT_FOUND.value()
                        errorResponse.message shouldBe NOT_FOUND_MEMBER.errorMessage()
                    }
                }
            }
        }
    }
}
