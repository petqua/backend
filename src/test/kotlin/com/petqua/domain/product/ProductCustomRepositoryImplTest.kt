package com.petqua.domain.product

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.product.ProductSourceType.HOME_RECOMMENDED
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.Sorter.REVIEW_COUNT_DESC
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.Sorter.SALE_PRICE_DESC
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.detail.DifficultyLevel.EASY
import com.petqua.domain.product.detail.OptimalTankSizeLiter
import com.petqua.domain.product.detail.OptimalTemperature
import com.petqua.domain.product.detail.ProductInfoRepository
import com.petqua.domain.product.detail.Temperament.PEACEFUL
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.product.dto.ProductSearchCondition
import com.petqua.domain.product.dto.ProductWithInfoResponse
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.category
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productInfo
import com.petqua.test.fixture.productRecommendation
import com.petqua.test.fixture.store
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest
class ProductCustomRepositoryImplTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val productInfoRepository: ProductInfoRepository,
    private val categoryRepository: CategoryRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "펫쿠아"))

    Given("Id로 상품과 상세정보를 함께 조회할 때") {
        val category = categoryRepository.save(
            category(
                family = "난태생과",
                species = "고정구피"
            )
        )
        val product = productRepository.save(
            product(
                name = "고정구피",
                storeId = store.id,
                categoryId = category.id,
                discountPrice = ZERO,
                reviewCount = 0,
                reviewTotalScore = 0
            )
        )
        val productInfo = productInfoRepository.save(
            productInfo(
                productId = product.id,
                categoryId = 0,
                optimalTemperature = OptimalTemperature(26, 28),
                difficultyLevel = EASY,
                optimalTankSizeLiter = OptimalTankSizeLiter(10, 50),
                temperament = PEACEFUL,
            )
        )

        When("Id를 입력하면") {
            val productWithInfoResponse = productRepository.findProductWithInfoByIdOrThrow(product.id) {
                ProductException(NOT_FOUND_PRODUCT)
            }

            Then("입력한 Id의 상품과 상세정보가 반환된다") {
                productWithInfoResponse shouldBe ProductWithInfoResponse(
                    product = product,
                    storeName = store.name,
                    productInfo = productInfo,
                    category = category
                )
            }
        }

        When("존재하지 않는 상품의 Id와 예외를 입력하면") {
            val invalidId = MIN_VALUE

            Then("입력한 예외를 던진다") {
                shouldThrow<ProductException> {
                    productRepository.findProductWithInfoByIdOrThrow(invalidId) { ProductException(NOT_FOUND_PRODUCT) }
                }.exceptionType() shouldBe NOT_FOUND_PRODUCT
            }
        }
    }


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
                paging = CursorBasedPaging(limit = 2),
            )

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 2
            }
        }

        When("높은 가격 순으로 조회하면") {
            val products = productRepository.findAllByCondition(
                condition = ProductReadCondition(sorter = SALE_PRICE_DESC),
                paging = CursorBasedPaging(),
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
                paging = CursorBasedPaging(),
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
                paging = CursorBasedPaging(),
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
                paging = CursorBasedPaging(),
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
                paging = CursorBasedPaging(),
            )

            Then("추천 상품이 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                )
            }
        }
    }

    Given("조건에 따라 상품의 개수를 셀 때") {
        val product1 = productRepository.save(product(name = "상품1", storeId = store.id))
        val product2 = productRepository.save(product(name = "상품2", storeId = store.id))

        recommendationRepository.save(productRecommendation(productId = product1.id))
        recommendationRepository.save(productRecommendation(productId = product2.id))

        When("추천 상품을 조회하면") {
            val count = productRepository.countByReadCondition(ProductReadCondition(sourceType = HOME_RECOMMENDED))

            Then("추천 상품의 개수를 반환한다") {
                count shouldBe 2
            }
        }
    }

    Given("다중 id로 ProductResponse를 조회 할 때") {
        val product1 = productRepository.save(product(name = "상품1", storeId = store.id))
        val product2 = productRepository.save(product(name = "상품2", storeId = store.id))

        When("id 목록을 입력하면") {
            val products = productRepository.findAllProductResponseByIdIn(listOf(product1.id, product2.id))

            Then("해당 id의 ProductResponse를 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }
    }

    Given("검색으로 상품을 조회할 때") {
        val product1 = productRepository.save(
            product(
                name = "상품1",
                storeId = store.id,
                discountPrice = ZERO,
                reviewCount = 0,
                reviewTotalScore = 0,
                canDeliverySafely = false,
                canDeliveryCommonly = false,
                canPickUp = true,
            )
        )
        val product2 = productRepository.save(
            product(
                name = "상품2",
                storeId = store.id,
                discountPrice = ONE,
                reviewCount = 1,
                reviewTotalScore = 1,
                canDeliverySafely = false,
                canDeliveryCommonly = true,
                canPickUp = true,
            )
        )
        val product3 = productRepository.save(
            product(
                name = "상품3",
                storeId = store.id,
                discountPrice = ONE,
                reviewCount = 1,
                reviewTotalScore = 5,
                canDeliverySafely = true,
                canDeliveryCommonly = false,
                canPickUp = true,
            )
        )
        val product4 = productRepository.save(
            product(
                name = "상품4",
                storeId = store.id,
                discountPrice = TEN,
                reviewCount = 2,
                reviewTotalScore = 10,
                canDeliverySafely = true,
                canDeliveryCommonly = true,
                canPickUp = true,
            )
        )

        When("상품 이름을 정확히 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "상품1"),
                paging = CursorBasedPaging()
            )

            Then("해당 이름의 상품을 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("상품 이름을 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "상품"),
                paging = CursorBasedPaging()
            )

            Then("관련된 이름의 상품들을 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("존재하지 않는 상품 이름을 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "NON EXISTENT PRODUCT"),
                paging = CursorBasedPaging()
            )

            Then("상품을 반환하지 않는다") {
                products shouldHaveSize 0
            }
        }

        When("상품 이름과 안전배송 조건을 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "상품", deliveryMethod = SAFETY),
                paging = CursorBasedPaging()
            )

            Then("입력한 조건에 맞는 상품들을 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                )
            }
        }

        When("상품 이름과 일반배송 조건을 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "상품", deliveryMethod = COMMON),
                paging = CursorBasedPaging()
            )

            Then("입력한 조건에 맞는 상품들을 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("상품 이름과 직접수령 조건을 입력하면") {
            val products = productRepository.findBySearch(
                condition = ProductSearchCondition(word = "상품", deliveryMethod = PICK_UP),
                paging = CursorBasedPaging()
            )

            Then("입력한 조건에 맞는 상품들을 반환한다") {
                products shouldContainExactly listOf(
                    ProductResponse(product4, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
