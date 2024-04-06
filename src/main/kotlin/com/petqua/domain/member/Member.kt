package com.petqua.domain.member

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.member.nickname.Nickname
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.SIGN_UP_NEEDED_MEMBER
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val authMemberId: Long,

    @Enumerated(EnumType.STRING)
    val authority: Authority,

    @Column(nullable = false)
    var nickname: Nickname,

    var profileImageUrl: String?,

    @Column(nullable = false)
    var fishTankCount: Int,

    @Column(nullable = false)
    var fishLifeYear: FishLifeYear,

    @Column(nullable = false)
    var hasProfile: Boolean,

    @Column(nullable = false)
    var hasAgreedToMarketingNotification: Boolean,

    @Column(nullable = false)
    var isDeleted: Boolean = false,
) {
    fun delete() {
        isDeleted = true
        anonymize()
    }

    private fun anonymize() {
        nickname = Nickname.emptyNickname()
        profileImageUrl = null
        hasProfile = false
        hasAgreedToMarketingNotification = false
    }

    fun updateNickname(nickname: Nickname) {
        this.nickname = nickname
    }

    fun updateFishLifeYear(fishLifeYear: FishLifeYear) {
        this.fishLifeYear = fishLifeYear;
    }

    fun updateHasProfile(hasProfile: Boolean) {
        this.hasProfile = hasProfile;
    }

    fun updateHasAgreedToMarketingNotification(hasAgreedToMarketingNotification: Boolean) {
        this.hasAgreedToMarketingNotification = hasAgreedToMarketingNotification;
    }

    fun increaseFishTankCount() {
        this.fishTankCount++;
    }

    fun validateHasSignedUp() {
        throwExceptionWhen(!isSignUpNeeded()) {
            MemberException(SIGN_UP_NEEDED_MEMBER)
        }
    }

    private fun isSignUpNeeded(): Boolean {
        return nickname.isEmpty()
    }

    companion object {
        fun emptyProfileMemberFrom(authMemberId: Long): Member {
            return Member(
                authority = MEMBER,
                authMemberId = authMemberId,
                nickname = Nickname.emptyNickname(),
                profileImageUrl = null,
                fishTankCount = 0,
                fishLifeYear = FishLifeYear.emptyFishLifeYear(),
                hasProfile = false,
                hasAgreedToMarketingNotification = false,
            )
        }
    }
}
