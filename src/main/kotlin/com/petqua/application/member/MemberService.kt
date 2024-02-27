package com.petqua.application.member

import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val cartProductRepository: CartProductRepository,
) {

    fun deleteBy(memberId: Long) {
        val member = memberRepository.findActiveByIdOrThrow(memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        member.delete()

        cartProductRepository.deleteByMemberId(member.id)
        refreshTokenRepository.deleteByMemberId(member.id)
    }
}
