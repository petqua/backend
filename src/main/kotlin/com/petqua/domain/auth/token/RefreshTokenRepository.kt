package com.petqua.domain.auth.token

import org.springframework.data.repository.CrudRepository

fun RefreshTokenRepository.findByTokenOrThrow(
    token: String,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("${this::class.java.name} entity 를 찾을 수 없습니다. token=$token") }
): RefreshToken {
    return findByToken(token) ?: throw exceptionSupplier()
}

interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): RefreshToken?

    fun deleteByMemberId(memberId: Long)
}
