package com.petqua.application.member

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.cartProduct
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class MemberServiceTest(
    private val memberService: MemberService,
    private val memberRepository: MemberRepository,
    private val cartProductRepository: CartProductRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("회원을 삭제할 때") {
        val member = memberRepository.save(
            member(nickname = "회원")
        )
        val refreshToken = refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = "xxx.yyy.zzz"
            )
        )
        val cartProduct = cartProductRepository.save(
            cartProduct(
                memberId = member.id,
                productId = 1
            )
        )

        When("삭제할 회원을 입력하면") {
            memberService.deleteBy(member.id)

            Then("회원과 회원 관련 데이터들이 삭제된다") {
                val deletedMember = memberRepository.findByIdOrThrow(member.id)

                assertSoftly(deletedMember) {
                    it.isDeleted shouldBe true
                    it.nickname shouldBe ""

                    cartProductRepository.existsById(cartProduct.id) shouldBe false
                    refreshTokenRepository.existsById(refreshToken.id) shouldBe false
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
