package com.petqua.domain.auth.token

import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {

    fun existsByToken(token: String): Boolean

    fun deleteByMemberId(memberId: Long)
}
