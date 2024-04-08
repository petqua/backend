package com.petqua.domain.auth

import org.springframework.data.jpa.repository.JpaRepository

interface AuthCredentialsRepository : JpaRepository<AuthCredentials, Long> {

    fun findByOauthIdAndOauthServerNumberAndIsDeletedFalse(oauthId: Long, oauthServerNumber: Int): AuthCredentials?
}
