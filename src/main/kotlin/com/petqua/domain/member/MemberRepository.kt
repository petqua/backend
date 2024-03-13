package com.petqua.domain.member

import org.springframework.data.jpa.repository.JpaRepository

fun MemberRepository.findByAuthMemberIdOrThrow(
    authMemberId: Long,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${Member::class.java.name} entity 를 찾을 수 없습니다.") },
): Member {
    return findByAuthMemberId(authMemberId) ?: throw exceptionSupplier()
}

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByAuthMemberId(authMemberId: Long): Member?
}
