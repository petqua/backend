package com.petqua.domain.member

import jakarta.persistence.*

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val oauthId: String,

    @Column(nullable = false)
    val oauthServerNumber: Int
)
