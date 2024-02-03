package com.petqua.application.product

import com.petqua.application.product.dto.DeleteWishCommand
import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.SaveWishCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType
import com.petqua.exception.product.WishProductException
import com.petqua.exception.product.WishProductExceptionType
import com.petqua.presentation.product.WishProductResponse
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import com.petqua.test.fixture.wishProduct
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WishProductServiceTest(
    private val wishProductService: WishProductService,
    private val wishProductRepository: WishProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val storeRepository: StoreRepository,
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
            wishProductService.save(command)

            Then("찜 상품이 추가되고, 상품의 찜 개수가 증가한다") {
                wishProductRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe true

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount.plus()
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
                    wishProductService.save(command)
                }.exceptionType() shouldBe MemberExceptionType.NOT_FOUND_MEMBER
            }
        }

        When("상품이 존재하지 않으면") {
            val command = SaveWishCommand(
                memberId = member.id,
                productId = Long.MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductException> {
                    wishProductService.save(command)
                }.exceptionType() shouldBe ProductExceptionType.NOT_FOUND_PRODUCT
            }
        }

        When("이미 해당 상품이 찜으로 등록되어 있다면") {
            val command = SaveWishCommand(
                memberId = member.id,
                productId = product.id
            )
            wishProductService.save(command)

            Then("예외가 발생한다") {
                shouldThrow<WishProductException> {
                    wishProductService.save(command)
                }.exceptionType() shouldBe WishProductExceptionType.ALREADY_EXIST_WISH_PRODUCT
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
        val wishProduct = wishProductRepository.save(wishProduct())
        val command = DeleteWishCommand(
            memberId = member.id,
            wishProductId = wishProduct.id
        )

        When("요청하면") {
            wishProductService.delete(command)

            Then("찜 상품이 삭제되고, 상품의 찜 개수가 감소한다") {
                wishProductRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe false

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount.minus()
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
        val wishProduct = wishProductRepository.save(wishProduct())

        When("해당 찜 상품이 존재 하지 않으면") {
            val command = DeleteWishCommand(
                memberId = member.id,
                wishProductId = Long.MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<WishProductException> {
                    wishProductService.delete(command)
                }.exceptionType() shouldBe WishProductExceptionType.NOT_FOUND_WISH_PRODUCT
            }
        }

        When("요청한 멤버가 해당 찜 상품의 주인이 아닐시") {
            val command = DeleteWishCommand(
                memberId = Long.MIN_VALUE,
                wishProductId = wishProduct.id
            )

            Then("예외가 발생한다") {
                shouldThrow<WishProductException> {
                    wishProductService.delete(command)
                }.exceptionType() shouldBe WishProductExceptionType.FORBIDDEN_WISH_PRODUCT
            }
        }
    }

    Given("찜 목록 조회를") {
        val member = memberRepository.save(member())
        val store = storeRepository.save(store())
        val (product1, product2, product3) = saveProducts(productRepository, store)

        val wish1 = wishProductRepository.save(wishProduct(productId = product3.id))
        val wish2 = wishProductRepository.save(wishProduct(productId = product2.id))
        val wish3 = wishProductRepository.save(wishProduct(productId = product1.id))

        When("요청하면") {
            val command = ReadAllWishProductCommand(
                memberId = member.id
            )
            val responses = wishProductService.readAll(command)

            Then("찜 목록이 찜 등록 순서대로 반환된다") {
                responses shouldBe listOf(
                    WishProductResponse(wish3.id, product1, store.name),
                    WishProductResponse(wish2.id, product2, store.name),
                    WishProductResponse(wish1.id, product3, store.name)
                )
            }
        }
    }

    Given("찜 목록 조회시") {
        memberRepository.save(member())
        val store = storeRepository.save(store())
        val (product1, product2, product3) = saveProducts(productRepository, store)
        wishProductRepository.save(wishProduct(productId = product3.id))
        wishProductRepository.save(wishProduct(productId = product2.id))
        wishProductRepository.save(wishProduct(productId = product1.id))

        When("멤버가 존재하지 않으면") {
            val command = ReadAllWishProductCommand(
                memberId = Long.MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    wishProductService.readAll(command)
                }.exceptionType() shouldBe MemberExceptionType.NOT_FOUND_MEMBER
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})

private fun saveProducts(
    productRepository: ProductRepository,
    store: Store
): Triple<Product, Product, Product> {
    val product1 = productRepository.save(
        product(
            name = "상품1",
            storeId = store.id,
            discountPrice = BigDecimal.ZERO,
            reviewCount = 0,
            reviewTotalScore = 0
        )
    )
    val product2 = productRepository.save(
        product(
            name = "상품2",
            storeId = store.id,
            discountPrice = BigDecimal.ONE,
            reviewCount = 1,
            reviewTotalScore = 1
        )
    )
    val product3 = productRepository.save(
        product(
            name = "상품3",
            storeId = store.id,
            discountPrice = BigDecimal.ONE,
            reviewCount = 1,
            reviewTotalScore = 5
        )
    )
    return Triple(product1, product2, product3)
}
