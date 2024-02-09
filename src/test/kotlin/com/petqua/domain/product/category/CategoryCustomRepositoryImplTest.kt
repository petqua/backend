package com.petqua.domain.product.category

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
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

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
                canDeliverySafely = false,
                canDeliveryCommonly = false,
                canPickUp = true,
            )
        )
        val product2 = productRepository.save(
            product(
                name = "팬시구피",
                storeId = store.id,
                categoryId = category2.id,
                discountPrice = BigDecimal.ONE,
                reviewCount = 2,
                canDeliverySafely = false,
                canDeliveryCommonly = true,
                canPickUp = true,
            )
        )
        val product3 = productRepository.save(
            product(
                name = "팬시구피 세트",
                storeId = store.id,
                categoryId = category2.id,
                discountPrice = BigDecimal.TEN,
                reviewCount = 1,
                canDeliverySafely = true,
                canDeliveryCommonly = true,
                canPickUp = false,
            )
        )

        When("어과를 입력하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과"),
                paging = CategoryProductPaging()
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
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    species = "팬시구피"
                ),
                paging = CategoryProductPaging()
            )

            Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("안전배송 조건을 입력하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    canDeliverSafely = true
                ),
                paging = CategoryProductPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                )
            }
        }

        When("일반배송 조건을 입력하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    canDeliverCommonly = true
                ),
                paging = CategoryProductPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("직접수령 조건을 입력하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(
                    family = "송사리과",
                    canPickUp = true
                ),
                paging = CategoryProductPaging()
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                products shouldContainExactly listOf(
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("개수 제한을 입력하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과"),
                paging = CategoryProductPaging(limit = 1)
            )

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 1
            }
        }

        When("높은 가격 순으로 조회하면") {
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = SALE_PRICE_DESC),
                paging = CategoryProductPaging()
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
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = SALE_PRICE_ASC),
                paging = CategoryProductPaging()
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
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = REVIEW_COUNT_DESC),
                paging = CategoryProductPaging()
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
            val products = categoryRepository.findProductsByCondition(
                condition = CategoryProductReadCondition(family = "송사리과", sorter = ENROLLMENT_DATE_DESC),
                paging = CategoryProductPaging()
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

    afterContainer {
        dataCleaner.clean()
    }
})
