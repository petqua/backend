package com.petqua.domain.member

import com.petqua.common.domain.BaseEntity
import com.petqua.common.domain.SoftDeleteEntity
import com.petqua.domain.auth.Authority
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

private const val DELETED_MEMBER_NAME = "탈퇴한 회원"

@Entity
class Member(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val oauthId: String,

    @Column(nullable = false)
    val oauthServerNumber: Int,

    @Enumerated(STRING)
    val authority: Authority,

    @Column(nullable = false)
    var nickname: String = "쿠아", // FIXME: 회원 닉네임 정책 추가

    val profileImageUrl: String? = null,

    @Column(nullable = false)
    val fishBowlCount: Int = 0,

    @Column(nullable = false)
    val years: Int = 1,

    @Column(nullable = false)
    var isDeleted: Boolean = false,
) : BaseEntity(), SoftDeleteEntity {

    fun delete() {
        isDeleted = true
        anonymize()
    }

    private fun anonymize() {
        nickname = DELETED_MEMBER_NAME
    }

    override fun validateDeleted() {
        if (isDeleted) {
            throw MemberException(MemberExceptionType.NOT_FOUND_MEMBER)
        }
    }
}
