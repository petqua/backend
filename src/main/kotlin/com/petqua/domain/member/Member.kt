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
    val authority: Authority
)
