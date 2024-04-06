package com.petqua.domain.auth

import org.springframework.data.jpa.repository.JpaRepository

interface AuthMemberRepository : JpaRepository<AuthMember, Long> {

    fun findByOauthIdAndOauthServerNumberAndIsDeletedFalse(oauthId: Long, oauthServerNumber: Int): AuthMember?
}
