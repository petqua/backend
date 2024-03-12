package com.petqua.domain.member

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class MemberDetail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var nickname: String = "쿠아", // FIXME: 회원 닉네임 정책 추가

    var profileImageUrl: String? = null,

    @Column(nullable = false)
    val fishTankCount: Int = 0,

    @Column(nullable = false)
    val fishLifeYear: FishLifeYear = FishLifeYear.forAnonymous(),

    @Column(nullable = false)
    var isAnonymous: Boolean = true,

    @Column(nullable = false)
    var hasAgreedToMarketingNotification: Boolean = false,

    @Column(nullable = false)
    var isDeleted: Boolean = false,
) {
}
