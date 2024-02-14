package com.petqua.domain.member

import com.petqua.domain.auth.Authority
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val oauthId: String,

    @Column(nullable = false)
    val oauthServerNumber: Int,

    @Enumerated(STRING)
    val authority: Authority,

    @Column(nullable = false)
    val nickname: String = "쿠아", // FIXME: 회원 닉네임 정책 추가

    val profileImageUrl: String? = null,

    @Column(nullable = false)
    val fishBowlCount: Int = 0,

    @Column(nullable = false)
    val years: Int = 1,
)
