package com.petqua.domain.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByOauthIdAndOauthServerNumber(oauthId: String, oauthServerNumber: Int): Member?
}
