package com.petqua.application.wish

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.wish.WishRepository
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class WishServiceTest(
    val wishService: WishService,
    val wishRepository: WishRepository,
    val productRepository: ProductRepository,
    val memberRepository: MemberRepository,
) : BehaviorSpec({

    Given("Wish 생성을") {
        val originalWishCount = 0
        val product = productRepository.save(
            product(
                wishCount = originalWishCount
            )
        )
        val member = memberRepository.save(member())
        val command = SaveWishCommand(
            memberId = member.id,
            productId = product.id
        )

        When("요청하면") {
            wishService.save(command)

            Then("Wish가 생성되고, Product의 WishCount가 증가한다") {
                wishRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe true

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount + 1
            }
        }
    }
})
