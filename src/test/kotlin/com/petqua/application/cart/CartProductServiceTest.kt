package com.petqua.application.cart

import com.petqua.application.cart.dto.DeleteCartProductCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.DUPLICATED_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.FORBIDDEN_CART_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.NOT_FOUND_CART_PRODUCT
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.cartProduct
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.saveCartProductCommand
import com.petqua.test.fixture.store
import com.petqua.test.fixture.updateCartProductOptionCommand
import io.kotest.assertions.assertSoftly
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
    private val storeRepository: StoreRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("봉달 상품 저장 명령으로") {
        val productId = productRepository.save(
            product(
                id = 1L,
                commonDeliveryFee = 3000.toBigDecimal(),
                safeDeliveryFee = 5000.toBigDecimal()
            )
        ).id
        val memberId = memberRepository.save(member(id = 1L)).id
        val command = saveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = 1,
            sex = MALE,
            deliveryMethod = COMMON,
            deliveryFee = 3000.toBigDecimal(),
        )

        When("봉달 상품을") {
            cartProductService.save(command)

            Then("저장할 수 있다") {
                cartProductRepository.findAll().size shouldBe 1
            }
        }
    }

    Given("봉달 상품 저장시") {
        val productId = productRepository.save(product(id = 1L, commonDeliveryFee = 3000.toBigDecimal())).id
        val memberId = memberRepository.save(member(id = 1L)).id

        When("존재 하지 않는 회원이 요청 하는 경우") {
            val command = saveCartProductCommand(
                memberId = Long.MIN_VALUE,
                productId = productId,
                quantity = 1,
                sex = MALE,
                deliveryMethod = COMMON,
                deliveryFee = 3000.toBigDecimal(),
            )
            Then("예외가 발생 한다") {
                shouldThrow<MemberException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }

        When("존재 하지 않는 상품이 요청 하는 경우") {
            val command = saveCartProductCommand(
                memberId = memberId,
                productId = Long.MIN_VALUE,
                quantity = 1,
                sex = MALE,
                deliveryMethod = COMMON,
                deliveryFee = 3000.toBigDecimal(),
            )
            Then("예외가 발생 한다") {
                shouldThrow<ProductException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT
            }
        }

        When("중복 상품이 요청 하는 경우") {
            val command = saveCartProductCommand(
                memberId = memberId,
                productId = productId,
                quantity = 1,
                sex = MALE,
                deliveryMethod = COMMON,
                deliveryFee = 3000.toBigDecimal(),
            )
            cartProductService.save(command)
            Then("예외가 발생 한다") {
                shouldThrow<CartProductException> {
                    cartProductService.save(command)
                }.exceptionType() shouldBe DUPLICATED_PRODUCT
            }
        }
    }

    Given("봉달 상품 옵션 수정 명령으로") {
        val productId = productRepository.save(
            product(
                id = 1L, commonDeliveryFee = 3000.toBigDecimal(), safeDeliveryFee = 5000.toBigDecimal()
            )
        ).id
        val memberId = memberRepository.save(member(id = 1L)).id
        val cartProduct = cartProductRepository.save(
            cartProduct(
                memberId = memberId,
                productId = productId,
                deliveryMethod = COMMON
            )
        )

        val command = updateCartProductOptionCommand(
            cartProductId = cartProduct.id,
            memberId = memberId,
            quantity = 2,
            sex = FEMALE,
            deliveryMethod = SAFETY,
            deliveryFee = 5000.toBigDecimal(),
        )

        When("봉달 상품 옵션을") {
            cartProductService.updateOptions(command)

            Then("수정할 수 있다") {
                val savedCartProduct = cartProductRepository.findByIdOrThrow(cartProduct.id)
                assertSoftly(savedCartProduct) {
                    quantity shouldBe CartProductQuantity(2)
                    sex shouldBe FEMALE
                    deliveryMethod shouldBe SAFETY
                }
            }
        }
    }

    Given("봉달 상품 옵션 수정시") {
        val productId = productRepository.save(
            product(
                commonDeliveryFee = 3000.toBigDecimal(),
                safeDeliveryFee = 5000.toBigDecimal()
            )
        ).id
        val memberId = memberRepository.save(member()).id
        val cartProduct = cartProductRepository.save(
            cartProduct(
                memberId = memberId,
                productId = productId,
                deliveryMethod = COMMON
            )
        )

        When("존재 하지 않는 장바구니 상품에 수정 요청 하는 경우") {
            val command = updateCartProductOptionCommand(
                cartProductId = Long.MIN_VALUE,
                memberId = memberId,
                quantity = 2,
                sex = FEMALE,
                deliveryMethod = SAFETY,
                deliveryFee = 5000.toBigDecimal(),
            )
            Then("예외가 발생 한다") {
                shouldThrow<CartProductException> {
                    cartProductService.updateOptions(command)
                }.exceptionType() shouldBe NOT_FOUND_CART_PRODUCT
            }
        }

        When("이미 담겨있는 옵션 상품으로 수정 하는 경우") {
            cartProductRepository.save(
                cartProduct(
                    memberId = memberId,
                    productId = productId,
                    sex = FEMALE,
                    deliveryMethod = SAFETY,
                    deliveryFee = 5000.toBigDecimal(),
                )
            )
            val command = updateCartProductOptionCommand(
                cartProductId = cartProduct.id,
                memberId = memberId,
                quantity = 3,
                sex = FEMALE,
                deliveryMethod = SAFETY,
                deliveryFee = 5000.toBigDecimal(),
            )
            Then("예외가 발생 한다") {
                shouldThrow<CartProductException> {
                    cartProductService.updateOptions(command)
                }.exceptionType() shouldBe DUPLICATED_PRODUCT
            }
        }
    }

    Given("봉달 상품 삭제 명령으로") {
        val productId = productRepository.save(
            product(
                commonDeliveryFee = 3000.toBigDecimal(),
                safeDeliveryFee = 5000.toBigDecimal()
            )
        ).id
        val memberId = memberRepository.save(member()).id
        val cartProduct = cartProductRepository.save(
            cartProduct(
                memberId = memberId,
                productId = productId,
                deliveryMethod = COMMON
            )
        )

        When("봉달 상품을") {
            DeleteCartProductCommand(
                memberId = memberId,
                cartProductId = cartProduct.id
            ).let(cartProductService::delete)

            Then("삭제할 수 있다") {
                cartProductRepository.findAll().size shouldBe 0
            }
        }
    }

    Given("봉달 상품 삭제시") {
        val productId = productRepository.save(product()).id
        val memberId = memberRepository.save(member()).id
        val cartProduct = cartProductRepository.save(
            cartProduct(
                memberId = memberId,
                productId = productId,
                deliveryMethod = COMMON
            )
        )

        When("존재 하지 않는 장바구니 상품에 삭제 요청 하는 경우") {
            val command = DeleteCartProductCommand(
                memberId = memberId,
                cartProductId = Long.MIN_VALUE
            )
            Then("예외가 발생 한다") {
                shouldThrow<CartProductException> {
                    cartProductService.delete(command)
                }.exceptionType() shouldBe NOT_FOUND_CART_PRODUCT
            }
        }

        When("다른 회원이 장바구니 상품을 삭제 하는 경우") {
            val command = DeleteCartProductCommand(
                memberId = Long.MIN_VALUE,
                cartProductId = cartProduct.id
            )
            Then("예외가 발생 한다") {
                shouldThrow<CartProductException> {
                    cartProductService.delete(command)
                }.exceptionType() shouldBe FORBIDDEN_CART_PRODUCT
            }
        }
    }

    Given("봉달 상품 조회시") {
        val store = storeRepository.save(store(name = "store"))
        val productAId = productRepository.save(product(storeId = store.id)).id
        val productBId = productRepository.save(product(storeId = store.id)).id
        val productCId = productRepository.save(product(storeId = store.id)).id
        val memberId = memberRepository.save(member()).id
        cartProductRepository.saveAll(
            listOf(
                cartProduct(memberId = memberId, productId = productAId),
                cartProduct(memberId = memberId, productId = productBId),
                cartProduct(memberId = memberId, productId = productCId),
            )
        )

        When("봉달 상품이 있는 회원이 조회 하는 경우") {
            val result = cartProductService.readAll(memberId)

            Then("봉달 상품 리스트를 반환 한다") {
                result.size shouldBe 3
            }
        }

        When("봉달 상품이 없는 회원이 조회 하는 경우") {
            val newMemberId = memberRepository.save(member()).id
            val results = cartProductService.readAll(newMemberId)

            Then("빈 리스트를 반환 한다") {
                results.size shouldBe 0
            }
        }

        When("봉달에 담아둔 상품이 삭제된 경우") {
            productRepository.deleteById(productAId)
            val results = cartProductService.readAll(memberId)

            Then("상품의 판매 여부를 포함한 리스트를 반환 한다") {
                assertSoftly(results) {
                    size shouldBe 3
                    find { it.productId == 0L }!!.isOnSale shouldBe false
                }
            }
        }

        When("존재 하지 않는 회원이 조회 하는 경우") {
            Then("예외가 발생 한다") {
                shouldThrow<MemberException> {
                    cartProductService.readAll(Long.MIN_VALUE)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
