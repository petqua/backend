package com.petqua.domain.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByOauthIdAndOauthServerNumberAndIsDeletedFalse(oauthId: Long, oauthServerNumber: Int): Member?
}
