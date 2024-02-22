package com.petqua.domain.product.category

import com.petqua.common.domain.dto.CursorBasedPaging
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.Sorter.REVIEW_COUNT_DESC
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.Sorter.SALE_PRICE_DESC
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.category
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CategoryCustomRepositoryImplTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val categoryRepository: CategoryRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("카테고리 조건에 따라 상품을 조회할 때") {
        val store = storeRepository.save(store(name = "펫쿠아"))
        val category1 = categoryRepository.save(category(family = "송사리과", species = "고정구피"))
        val category2 = categoryRepository.save(category(family = "송사리과", species = "팬시구피"))

        val product1 = productRepository.save(
            product(
                name = "고정구피",
                storeId = store.id,
                categoryId = category1.id,
                discountPrice = BigDecimal.ZERO,
                reviewCount = 0,
                safeDeliveryFee = null,
                commonDeliveryFee = null,
                pickUpDeliveryFee = 0.toBigDecimal(),
            )
        )
        val product2 = productRepository.save(
            product(
                name = "팬시구피",
                storeId = store.id,
                categoryId = category2.id,
                discountPrice = BigDecimal.ONE,
                reviewCount = 2,
                safeDeliveryFee = null,
                commonDeliveryFee = 3000.toBigDecimal(),
                pickUpDeliveryFee = 0.toBigDecimal(),
            )
        )
        val product3 = productRepository.save(
            product(
                name = "팬시구피 세트",
                storeId = store.id,
                categoryId = category2.id,
                discountPrice = BigDecimal.TEN,
                reviewCount = 1,
                safeDeliveryFee = 5000.toBigDecimal(),
                commonDeliveryFee = 3000.toBigDecimal(),
                pickUpDeliveryFee = null,
            )
        )

        When("어과를 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과"),
                paging = CursorBasedPaging()
            )

            Then("입력한 어과에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("어과와 어종을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    species = listOf("팬시구피")
                ),
                paging = CursorBasedPaging()
            )

            Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("어과와 여러 어종을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    species = listOf("팬시구피", "고정구피")
                ),
                paging = CursorBasedPaging()
            )

            Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("안전배송 조건을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = SAFETY
                ),
                paging = CursorBasedPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                )
            }
        }

        When("일반배송 조건을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = COMMON
                ),
                paging = CursorBasedPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("직접수령 조건을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = PICK_UP
                ),
                paging = CursorBasedPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("개수 제한을 입력하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과"),
                paging = CursorBasedPaging(limit = 1)
            )

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 1
            }
        }

        When("높은 가격 순으로 조회하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = SALE_PRICE_DESC),
                paging = CursorBasedPaging()
            )

            Then("높은 가격순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("낮은 가격 순으로 조회하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = SALE_PRICE_ASC),
                paging = CursorBasedPaging()
            )

            Then("낮은 가격순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product1, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product3, store.name),
                )
            }
        }

        When("리뷰 많은 순으로 조회하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = REVIEW_COUNT_DESC),
                paging = CursorBasedPaging()
            )

            Then("리뷰 많은 순, 최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product2, store.name),
                    ProductResponse(product3, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("최신 등록 순으로 조회하면") {
            val products = categoryRepository.findProductsByCategoryCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = ENROLLMENT_DATE_DESC),
                paging = CursorBasedPaging()
            )

            Then("최신 등록 순으로 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }
    }

    Given("카테고리 조건에 따라 조회하는 상품의 개수를 셀 때") {
        val store = storeRepository.save(store(name = "펫쿠아"))
        val category1 = categoryRepository.save(category(family = "송사리과", species = "고정구피"))
        val category2 = categoryRepository.save(category(family = "송사리과", species = "팬시구피"))

        productRepository.saveAll(
            listOf(
                product(
                    name = "고정구피",
                    storeId = store.id,
                    categoryId = category1.id,
                    safeDeliveryFee = null,
                    commonDeliveryFee = null,
                    pickUpDeliveryFee = 0.toBigDecimal(),
                ),
                product(
                    name = "팬시구피",
                    storeId = store.id,
                    categoryId = category2.id,
                    safeDeliveryFee = null,
                    commonDeliveryFee = 3000.toBigDecimal(),
                    pickUpDeliveryFee = 0.toBigDecimal(),
                ),
                product(
                    name = "팬시구피 세트",
                    storeId = store.id,
                    categoryId = category2.id,
                    safeDeliveryFee = 5000.toBigDecimal(),
                    commonDeliveryFee = 3000.toBigDecimal(),
                    pickUpDeliveryFee = null,
                )
            )
        )

        When("어과를 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                )
            )

            Then("입력한 어과에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 3
            }
        }

        When("어과와 어종을 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                    species = listOf("팬시구피")
                ),
            )

            Then("입력한 어과와 어종에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 2
            }
        }

        When("어과와 여러 어종을 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                    species = listOf("팬시구피", "고정구피")
                ),
            )

            Then("입력한 어과와 어종에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 3
            }
        }

        When("안전배송 조건을 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = SAFETY
                )
            )

            Then("입력한 배송 조건에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 1
            }
        }

        When("일반배송 조건을 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = COMMON
                )
            )

            Then("입력한 배송 조건에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 2
            }
        }

        When("직접수령 조건을 입력하면") {
            val totalProductsCount = categoryRepository.countProductsByCategoryCondition(
                CategoryProductReadCondition(
                    family = "송사리과",
                    deliveryMethod = PICK_UP
                )
            )

            Then("입력한 배송 조건에 해당하는 상품의 개수가 반환된다") {
                totalProductsCount shouldBe 2
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
