package com.petqua.domain.auth.token

import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {

    fun existsByToken(token: String): Boolean

    fun findByToken(token: String): RefreshToken?

    fun deleteByMemberId(memberId: Long)
}
