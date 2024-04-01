package com.petqua.domain.auth

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.SoftDeleteEntity
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType
import com.petqua.exception.member.MemberExceptionType.INVALID_MEMBER_STATE
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

private const val DELETED_AUTH_FIELD = ""
private const val DELETED_OAUTH_ID = -1L

@Entity
class AuthCredentials(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var oauthId: Long,

    @Column(nullable = false)
    val oauthServerNumber: Int,

    var oauthAccessToken: String = "",

    var oauthAccessTokenExpiresAt: LocalDateTime? = null,

    var oauthRefreshToken: String = "",

    @Column(nullable = false)
    var isDeleted: Boolean = false,
) : BaseEntity(), SoftDeleteEntity {

    val oauthServerType: OauthServerType
        get() = OauthServerType.numberOf(oauthServerNumber)

    fun delete() {
        isDeleted = true
        anonymize()
    }

    private fun anonymize() {
        oauthId = DELETED_OAUTH_ID
        oauthAccessToken = DELETED_AUTH_FIELD
        oauthAccessTokenExpiresAt = null
        oauthRefreshToken = DELETED_AUTH_FIELD
    }

    override fun validateDeleted() {
        if (isDeleted) {
            throw MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        }
    }

    fun updateOauthToken(
        accessToken: String,
        expiresIn: Long,
        refreshToken: String?,
    ) {
        oauthAccessToken = accessToken
        oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(expiresIn)
        oauthRefreshToken = refreshToken ?: oauthRefreshToken
    }

    fun hasExpiredOauthToken(): Boolean {
        return oauthAccessTokenExpiresAt?.let { it < LocalDateTime.now() }
            ?: throw MemberException(INVALID_MEMBER_STATE)
    }

    companion object {
        fun authMemberOf(
            oauthId: Long,
            oauthServerNumber: Int,
        ): AuthCredentials {
            return AuthCredentials(
                oauthId = oauthId,
                oauthServerNumber = oauthServerNumber,
            )
        }
    }
}
