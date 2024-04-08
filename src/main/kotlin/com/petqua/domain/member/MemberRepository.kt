package com.petqua.domain.member

import com.petqua.domain.member.nickname.Nickname
import org.springframework.data.jpa.repository.JpaRepository

fun MemberRepository.findByAuthCredentialsIdOrThrow(
    authCredentialsId: Long,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${Member::class.java.name} entity 를 찾을 수 없습니다.") },
): Member {
    return findByAuthCredentialsId(authCredentialsId) ?: throw exceptionSupplier()
}


interface MemberRepository : JpaRepository<Member, Long> {

    fun findByAuthCredentialsId(authCredentialsId: Long): Member?

    fun existsMemberByNickname(nickname: Nickname): Boolean

    fun existsMemberByAuthCredentialsId(authCredentialsId: Long): Boolean
}
