package com.petqua.domain.member

import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.member.nickname.Nickname
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
    var nickname: Nickname = Nickname.emptyNickname(),

    var profileImageUrl: String? = null,

    @Column(nullable = false)
    var fishTankCount: Int = 0,

    @Column(nullable = false)
    var fishLifeYear: FishLifeYear = FishLifeYear.emptyFishLifeYear(),

    @Column(nullable = false)
    var hasProfile: Boolean = true,

    @Column(nullable = false)
    var hasAgreedToMarketingNotification: Boolean = false,

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

    companion object {
        fun emptyProfileMemberFrom(authMemberId: Long): Member {
            return Member(
                authority = MEMBER,
                authMemberId = authMemberId
            )
        }
    }
}
