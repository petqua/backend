package com.petqua.application.cart

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.cart.DeliveryMethod
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.exception.cart.CartProductExceptionType.DUPLICATED_PRODUCT
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class CartProductServiceTest(
    private val cartProductService: CartProductService,
    private val cartProductRepository: CartProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("봉달 상품 저장 명령으로") {
        val productId = productRepository.save(product(id = 1L)).id
        val memberId = memberRepository.save(member(id = 1L)).id
        val command = SaveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = 1,
            isMale = true,
            deliveryMethod = DeliveryMethod.COMMON,
        )

        When("봉달 상품을") {
            cartProductService.save(command)

            Then("저장할 수 있다") {
                cartProductRepository.findAll().size shouldBe 1
            }
        }
    }

    Given("봉달 상품 저장시") {
        val productId = productRepository.save(product(id = 1L)).id
        val memberId = memberRepository.save(member(id = 1L)).id

        When("존재 하지 않는 회원이 요청 하는 경우") {
            val command = SaveCartProductCommand(
                memberId = 999L,
                productId = productId,
                quantity = 1,
                isMale = true,
                deliveryMethod = DeliveryMethod.COMMON,
            )
            Then("예외가 발생 한다") {
                shouldThrow<MemberException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }

        When("존재 하지 않는 상품이 요청 하는 경우") {
            val command = SaveCartProductCommand(
                memberId = memberId,
                productId = 999L,
                quantity = 1,
                isMale = true,
                deliveryMethod = DeliveryMethod.COMMON,
            )
            Then("예외가 발생 한다") {
                shouldThrow<ProductException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT
            }
        }

        When("중복 상품이 요청 하는 경우") {
            val command = SaveCartProductCommand(
                memberId = memberId,
                productId = productId,
                quantity = 1,
                isMale = true,
                deliveryMethod = DeliveryMethod.COMMON,
            )
            cartProductService.save(command)
            Then("예외가 발생 한다") {
                shouldThrow<MemberException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe DUPLICATED_PRODUCT
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
