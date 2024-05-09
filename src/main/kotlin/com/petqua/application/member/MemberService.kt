package com.petqua.application.member

import com.petqua.application.member.dto.MemberAddProfileCommand
import com.petqua.application.member.dto.MemberSignUpCommand
import com.petqua.application.member.dto.UpdateProfileCommand
import com.petqua.application.token.AuthTokenInfo
import com.petqua.application.token.TokenService
import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.member.FishLifeYear
import com.petqua.domain.member.FishTankRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.PetFishRepository
import com.petqua.domain.member.nickname.Nickname
import com.petqua.domain.member.nickname.NicknameGenerator
import com.petqua.domain.member.nickname.NicknameWord
import com.petqua.domain.member.nickname.NicknameWordRepository
import com.petqua.domain.policy.bannedword.BannedWordRepository
import com.petqua.domain.policy.bannedword.BannedWords
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType
import com.petqua.exception.member.MemberExceptionType.ALREADY_EXIST_NICKNAME
import com.petqua.exception.member.MemberExceptionType.FAILED_NICKNAME_GENERATION
import com.petqua.exception.member.MemberExceptionType.HAS_SIGNED_UP_MEMBER
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val fishTankRepository: FishTankRepository,
    private val petFishRepository: PetFishRepository,
    private val fishRepository: FishRepository,
    private val nicknameWordRepository: NicknameWordRepository,
    private val nicknameGenerator: NicknameGenerator,
    private val bannedWordRepository: BannedWordRepository,
    private val tokenService: TokenService,
) {

    fun signUp(command: MemberSignUpCommand): AuthTokenInfo {
        validateDuplicateSignUp(command.authCredentialsId)

        val nicknameWords = nicknameWordRepository.findAll()
        val nickname = generateUniqueNickname(nicknameWords)
        val member = memberRepository.save(
            Member.emptyProfileMemberOf(
                authCredentialsId = command.authCredentialsId,
                nickname = nickname,
                hasAgreedToMarketingNotification = command.hasAgreedToMarketingNotification
            )
        )
        return tokenService.createAuthToken(member.id, member.authority)
    }

    private fun validateDuplicateSignUp(authCredentialsId: Long) {
        throwExceptionWhen(memberRepository.existsMemberByAuthCredentialsId(authCredentialsId)) {
            MemberException(HAS_SIGNED_UP_MEMBER)
        }
    }

    private fun generateUniqueNickname(nicknameWords: List<NicknameWord>): Nickname {
        repeat(nicknameGenerator.attemptCount()) {
            val nickname = nicknameGenerator.generate(nicknameWords)
            if (!memberRepository.existsMemberByNickname(nickname)) {
                return nickname
            }
        }
        throw MemberException(FAILED_NICKNAME_GENERATION)
    }

    fun addProfile(command: MemberAddProfileCommand) {
        val member = memberRepository.findActiveByIdOrThrow(command.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        member.updateFishLifeYear(FishLifeYear.from(command.fishLifeYear))
        member.increaseFishTankCount()

        validateContainingBannedWord(command.fishTankName)
        val fishTank = fishTankRepository.save(command.toFishTank(command.memberId))
        val petFishes = command.toPetFishes(command.memberId, fishTank.id)
        val countOfFishes = fishRepository.countsByIds(petFishes.uniqueIds())
        petFishes.validateFishesByCount(countOfFishes)
        petFishRepository.saveAll(petFishes.values)
    }

    fun validateContainingBannedWord(name: String) {
        val bannedWords = BannedWords(bannedWordRepository.findAll())
        bannedWords.validateContainingBannedWord(name)
    }

    fun updateProfile(command: UpdateProfileCommand) {
        validateNickname(command.nickname)
        val member = memberRepository.findActiveByIdOrThrow(command.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        member.updateNickname(Nickname.from(command.nickname))
    }

    private fun validateNickname(nickname: String) {
        validateContainingBannedWord(nickname)
        throwExceptionWhen(memberRepository.existsMemberByNickname(Nickname.from(nickname))) {
            MemberException(ALREADY_EXIST_NICKNAME)
        }
    }
}
