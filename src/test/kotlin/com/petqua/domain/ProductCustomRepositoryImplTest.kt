package com.petqua.domain

import com.petqua.application.ProductReadConditions
import com.petqua.application.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.application.Sorter.REVIEW_COUNT_DESC
import com.petqua.application.Sorter.SALE_PRICE_ASC
import com.petqua.application.Sorter.SALE_PRICE_DESC
import com.petqua.test.fixture.product
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO

@SpringBootTest
@Transactional
class ProductCustomRepositoryImplTest(
    @Autowired private val productRepository: ProductRepository,
) : BehaviorSpec({

    Given("조건에 따라 상품을 조회할 때") {
        val product1 = productRepository.save(product(name = "상품1", discountPrice = ZERO, reviewCount = 0))
        val product2 = productRepository.save(product(name = "상품2", discountPrice = ONE, reviewCount = 1))
        val product3 = productRepository.save(product(name = "상품3", discountPrice = TEN, reviewCount = 2))

        When("개수 제한을 입력하면") {
            val products: List<Product> = productRepository.findAllByConditions(ProductReadConditions(limit = 2))

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 2
            }
        }

        When("높은 가격 순으로 조회하면") {
            val products: List<Product> = productRepository.findAllByConditions(
                ProductReadConditions(
                    sorter = SALE_PRICE_DESC
                )
            )

            Then("높은 가격순으로 반환된다") {
                products shouldContainExactly listOf(product3, product2, product1)
            }
        }

        When("낮은 가격순으로 조회하면") {
            val products: List<Product> = productRepository.findAllByConditions(
                ProductReadConditions(
                    sorter = SALE_PRICE_ASC
                )
            )

            Then("낮은 가격순으로 반환된다") {
                products shouldContainExactly listOf(product1, product2, product3)
            }
        }

        When("리뷰 많은 순으로 조회하면") {
            val products: List<Product> = productRepository.findAllByConditions(
                ProductReadConditions(
                    sorter = REVIEW_COUNT_DESC
                )
            )

            Then("리뷰 많은 순으로 반환된다") {
                products shouldContainExactly listOf(product3, product2, product1)
            }
        }

        When("최신 등록 순으로 조회하면") {
            val products: List<Product> = productRepository.findAllByConditions(
                ProductReadConditions(
                    sorter = ENROLLMENT_DATE_DESC
                )
            )

            Then("최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(product3, product2, product1)
            }
        }
    }
})
