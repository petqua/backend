package com.petqua.application.wish

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.wish.WishRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.exception.wish.WishException
import com.petqua.exception.wish.WishExceptionType.ALREADY_EXIST_WISH
import com.petqua.exception.wish.WishExceptionType.FORBIDDEN_WISH
import com.petqua.exception.wish.WishExceptionType.NOT_FOUND_WISH
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.wish
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class WishServiceTest(
    private val wishService: WishService,
    private val wishRepository: WishRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("찜 상품 생성을") {
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

            Then("찜 상품이 추가되고, 상품의 찜 개수가 증가한다") {
                wishRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe true

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount + 1
            }
        }
    }

    Given("찜 상품 생성시") {
        val product = productRepository.save(product())
        val member = memberRepository.save(member())

        When("멤버가 존재하지 않으면") {
            val command = SaveWishCommand(
                memberId = Long.MIN_VALUE,
                productId = product.id
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    wishService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }

        When("상품이 존재하지 않으면") {
            val command = SaveWishCommand(
                memberId = member.id,
                productId = Long.MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductException> {
                    wishService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT
            }
        }

        When("이미 해당 상품이 찜으로 등록되어 있다면") {
            val command = SaveWishCommand(
                memberId = member.id,
                productId = product.id
            )
            wishService.save(command)

            Then("예외가 발생한다") {
                shouldThrow<WishException> {
                    wishService.save(command)
                }.exceptionType() shouldBe ALREADY_EXIST_WISH
            }
        }
    }

    Given("찜 상품 삭제를") {
        val product = productRepository.save(
            product(
                wishCount = 1
            )
        )
        val member = memberRepository.save(member())
        val wish = wishRepository.save(wish())
        val command = DeleteWishCommand(
            memberId = member.id,
            wishId = wish.id
        )

        When("요청하면") {
            wishService.delete(command)

            Then("찜 상품이 삭제되고, 상품의 찜 개수가 감소한다") {
                wishRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe false

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount - 1
            }
        }
    }

    Given("찜 삭제시") {
        productRepository.save(
            product(
                wishCount = 1
            )
        )
        val member = memberRepository.save(member())
        val wish = wishRepository.save(wish())

        When("해당 찜 상품이 존재 하지 않으면") {
            val command = DeleteWishCommand(
                memberId = member.id,
                wishId = Long.MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<WishException> {
                    wishService.delete(command)
                }.exceptionType() shouldBe NOT_FOUND_WISH
            }
        }

        When("요청한 멤버가 해당 찜 상품의 주인이 아닐시") {
            val command = DeleteWishCommand(
                memberId = Long.MIN_VALUE,
                wishId = wish.id
            )

            Then("예외가 발생한다") {
                shouldThrow<WishException> {
                    wishService.delete(command)
                }.exceptionType() shouldBe FORBIDDEN_WISH
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
