package com.petqua.domain

import com.petqua.domain.ProductSourceType.HOME_RECOMMENDED
import com.petqua.domain.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.Sorter.REVIEW_COUNT_DESC
import com.petqua.domain.Sorter.SALE_PRICE_ASC
import com.petqua.domain.Sorter.SALE_PRICE_DESC
import com.petqua.dto.ProductPaging
import com.petqua.dto.ProductReadCondition
import com.petqua.dto.ProductResponse
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productRecommendation
import com.petqua.test.fixture.store
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
    @Autowired private val storeRepository: StoreRepository,
    @Autowired private val recommendationRepository: ProductRecommendationRepository,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "펫쿠아"))

    Given("조건에 따라 상품을 조회할 때") {
        val product1 = productRepository.save(
            product(
                name = "상품1",
                storeId = store.id,
                discountPrice = ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val product2 = productRepository.save(
            product(
                name = "상품2",
                storeId = store.id,
                discountPrice = ONE,
                reviewCount = 1,
                reviewTotalScore = 1
            )
        )
        val product3 = productRepository.save(
            product(
                name = "상품3",
                storeId = store.id,
                discountPrice = ONE,
                reviewCount = 1,
                reviewTotalScore = 5
            )
        )
        val product4 = productRepository.save(
            product(
                name = "상품4",
                storeId = store.id,
                discountPrice = TEN,
                reviewCount = 2,
                reviewTotalScore = 10
            )
        )
        recommendationRepository.save(productRecommendation(productId = product1.id))

        When("개수 제한을 입력하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(),
                paging = ProductPaging(limit = 2),
            )

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 2
            }
        }

        When("높은 가격 순으로 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sorter = SALE_PRICE_DESC),
                paging = ProductPaging(),
            )

            Then("높은 가격순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("낮은 가격순으로 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sorter = SALE_PRICE_ASC),
                paging = ProductPaging(),
            )

            Then("낮은 가격순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product4, store.name),
                )
            }
        }

        When("리뷰 많은 순으로 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sorter = REVIEW_COUNT_DESC),
                paging = ProductPaging(),
            )

            Then("리뷰 많은 순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("최신 등록 순으로 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sorter = ENROLLMENT_DATE_DESC),
                paging = ProductPaging(),
            )

            Then("최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("추천 상품을 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sourceType = HOME_RECOMMENDED, sorter = ENROLLMENT_DATE_DESC),
                paging = ProductPaging(),
            )

            Then("추천 상품이 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                )
            }
        }
    }
})
