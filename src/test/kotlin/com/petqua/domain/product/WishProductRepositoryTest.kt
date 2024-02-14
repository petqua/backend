package com.petqua.domain.product

import com.petqua.domain.member.MemberRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.product
import com.petqua.test.fixture.wishProduct
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WishProductRepositoryTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val wishProductRepository: WishProductRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("상품 id와 회원 id로 찜 상품이 존재하는지 여부를 확인할 때") {
        val member = memberRepository.save(member())
        val product = productRepository.save(product())

        wishProductRepository.save(
            wishProduct(
                productId = product.id,
                memberId = member.id
            )
        )

        When("회원 id와 회원이 찜한 상품의 id을 입력하면") {
            val actual = wishProductRepository.existsByProductIdAndMemberId(
                productId = product.id,
                memberId = member.id
            )

            Then("true를 반환한다") {
                actual shouldBe true
            }
        }

        When("회원 id와 회원이 찜하지 않은 상품의 id을 입력하면") {
            val actual = wishProductRepository.existsByProductIdAndMemberId(
                productId = Long.MIN_VALUE,
                memberId = member.id
            )

            Then("false를 반환한다") {
                actual shouldBe false
            }
        }

        When("회원이 아닌 id를 입력하면") {
            val actual = wishProductRepository.existsByProductIdAndMemberId(
                productId = Long.MIN_VALUE,
                memberId = Long.MIN_VALUE
            )

            Then("false를 반환한다") {
                actual shouldBe false
            }
        }
    }

    Given("상품 id 목록과 회원 id로 찜 상품들을 조회할 때") {
        val member = memberRepository.save(member())
        val product1 = productRepository.save(product(name = "구피"))
        val product2 = productRepository.save(product(name = "고등어"))
        val product3 = productRepository.save(product(name = "방어"))
        val wishProduct1 = wishProductRepository.save(
            wishProduct(
                productId = product1.id,
                memberId = member.id
            )
        )
        val wishProduct2 = wishProductRepository.save(
            wishProduct(
                productId = product2.id,
                memberId = member.id
            )
        )

        When("회원 id와 상품 id 목록을 입력하면") {
            val wishedProductIds = wishProductRepository.findWishedProductIdByMemberIdAndProductIdIn(
                memberId = member.id,
                productIds = listOf(product1.id, product2.id, product3.id),
            )

            Then("입력한 상품 id 목록 중 회원이 찜한 상품들의 id들이 반환된다") {
                wishedProductIds shouldContainExactly listOf(
                    wishProduct1.productId,
                    wishProduct2.productId
                )
            }
        }
    }
    afterContainer {
        dataCleaner.clean()
    }
})
