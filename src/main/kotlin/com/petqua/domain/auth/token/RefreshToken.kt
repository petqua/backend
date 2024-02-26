package com.petqua.domain.auth.token

import com.petqua.common.domain.BaseEntity
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val memberId: Long = 0L,

    @Column(nullable = false)
    val token: String,
) : BaseEntity() {

    fun validateToken(other: String) {
        if (token != other) {
            throw AuthException(INVALID_REFRESH_TOKEN)
        }
    }
}
