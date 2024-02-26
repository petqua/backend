package com.petqua.domain.member

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.SoftDeleteEntity
import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

private const val DELETED_MEMBER_NAME = ""
private const val DELETED_MEMBER_OAUTH_ID = ""

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var oauthId: String,

    @Column(nullable = false)
    val oauthServerNumber: Int,

    @Enumerated(STRING)
    val authority: Authority,

    @Column(nullable = false)
    var nickname: String = "쿠아", // FIXME: 회원 닉네임 정책 추가

    var profileImageUrl: String? = null,

    @Column(nullable = false)
    val fishBowlCount: Int = 0,

    @Column(nullable = false)
    val years: Int = 1,

    @Column(nullable = false)
    var isDeleted: Boolean = false,

    var oauthAccessToken: String?,
    var expireAt: LocalDateTime?,
    var oauthRefreshToken: String?,
) : BaseEntity(), SoftDeleteEntity {


    fun delete() {
        isDeleted = true
        anonymize()
    }

    private fun anonymize() {
        oauthId = DELETED_MEMBER_OAUTH_ID
        nickname = DELETED_MEMBER_NAME
        profileImageUrl = null
        oauthAccessToken = null
        expireAt = null
        oauthRefreshToken = null
    }

    override fun validateDeleted() {
        if (isDeleted) {
            throw MemberException(NOT_FOUND_MEMBER)
        }
    }

    fun hasExpiredToken(): Boolean {
        return expireAt?.let { it < LocalDateTime.now() } ?: throw MemberException(NOT_FOUND_MEMBER)
    }

    fun updateToken(oauthTokenInfo: OauthTokenInfo): Member {
        oauthAccessToken = oauthTokenInfo.accessToken
        expireAt = LocalDateTime.now().plusSeconds(oauthTokenInfo.expiresIn)
        oauthRefreshToken = oauthTokenInfo.refreshToken ?: oauthRefreshToken
        return this
    }
}
