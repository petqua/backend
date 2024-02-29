package com.petqua.application.product

import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.UpdateWishCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.store.Store
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType
import com.petqua.presentation.product.dto.WishProductResponse
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import com.petqua.test.fixture.wishProduct
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import java.math.BigDecimal
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = NONE)
class WishProductServiceTest(
    private val wishProductService: WishProductService,
    private val wishProductRepository: WishProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val storeRepository: StoreRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("올바른 찜 상품 수정 요청시") {
        val originalWishCount = 1
        val product = productRepository.save(
            product(
                wishCount = originalWishCount
            )
        )
        val member = memberRepository.save(member())
        val command = UpdateWishCommand(
            memberId = member.id,
            productId = product.id
        )

        When("해당 상품이 찜 상태가 아니라면") {
            wishProductService.update(command)

            Then("찜 상품이 추가되고, 상품의 찜 개수가 증가한다") {
                wishProductRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe true

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount.increase()
            }
        }

        When("해당 상품이 이미 찜 상태라면") {
            wishProductRepository.save(wishProduct(productId = product.id, memberId = member.id))
            wishProductService.update(command)

            Then("찜 상품이 삭제되고, 상품의 찜 개수가 감소한다") {
                wishProductRepository.existsByProductIdAndMemberId(product.id, member.id) shouldBe false

                val updatedProduct = productRepository.findByIdOrThrow(product.id)
                updatedProduct.wishCount shouldBe product.wishCount.decrease()
            }
        }
    }

    Given("찜 상품 수정시") {
        val product = productRepository.save(product())
        val member = memberRepository.save(member())

        When("상품이 존재하지 않으면") {
            val command = UpdateWishCommand(
                memberId = member.id,
                productId = MIN_VALUE
            )

            Then("예외가 발생한다") {
                shouldThrow<ProductException> {
                    wishProductService.update(command)
                }.exceptionType() shouldBe ProductExceptionType.NOT_FOUND_PRODUCT
            }
        }
    }

    Given("찜 목록 조회를") {
        val member = memberRepository.save(member())
        val store = storeRepository.save(store())
        val (product1, product2, product3) = saveProducts(productRepository, store)

        val wish1 = wishProductRepository.save(wishProduct(productId = product3.id, memberId = member.id))
        val wish2 = wishProductRepository.save(wishProduct(productId = product2.id, memberId = member.id))
        val wish3 = wishProductRepository.save(wishProduct(productId = product1.id, memberId = member.id))

        When("요청하면") {
            val command = ReadAllWishProductCommand(
                memberId = member.id
            )
            val response = wishProductService.readAll(command)

            Then("찜 목록이 찜 등록 순서대로 반환된다") {
                response.totalProductsCount shouldBe 3
                response.hasNextPage shouldBe false
                response.products shouldBe listOf(
                    WishProductResponse(wish3.id, product1, store.name),
                    WishProductResponse(wish2.id, product2, store.name),
                    WishProductResponse(wish1.id, product3, store.name)
                )
            }
        }
    }

    Given("찜 목록 조회시") {
        val member = memberRepository.save(member())
        val store = storeRepository.save(store())
        val (product1, product2, product3) = saveProducts(productRepository, store)

        val wish1 = wishProductRepository.save(wishProduct(productId = product3.id, memberId = member.id))
        val wish2 = wishProductRepository.save(wishProduct(productId = product2.id, memberId = member.id))
        val wish3 = wishProductRepository.save(wishProduct(productId = product1.id, memberId = member.id))

        When("조회 개수 제한이 있는 상태에서 찜 목록 조회를 요청하면") {
            val command = ReadAllWishProductCommand(
                memberId = member.id,
                limit = 2
            )
            val response = wishProductService.readAll(command)

            Then("정해진 개수만큼 찜 목록이 찜 등록 순서대로 반환된다") {
                response.totalProductsCount shouldBe 3
                response.hasNextPage shouldBe true
                response.products shouldBe listOf(
                    WishProductResponse(wish3.id, product1, store.name),
                    WishProductResponse(wish2.id, product2, store.name),
                )
            }
        }

        When("마지막으로 조회한 찜 상품 아이디가 주어진 상태에서 찜 목록 조회를 요청하면") {
            val command = ReadAllWishProductCommand(
                memberId = member.id,
                lastViewedId = 3L,
            )
            val response = wishProductService.readAll(command)

            Then("마지막으로 조호한 찜 상품 이후부터 찜 목록이 찜 등록 순서대로 반환된다") {
                response.totalProductsCount shouldBe 3
                response.hasNextPage shouldBe false
                response.products shouldBe listOf(
                    WishProductResponse(wish2.id, product2, store.name),
                    WishProductResponse(wish1.id, product3, store.name),
                )
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})

private fun saveProducts(
    productRepository: ProductRepository,
    store: Store,
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
