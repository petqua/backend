package com.petqua.application.product.category

import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.category
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import java.math.BigDecimal

@SpringBootTest(webEnvironment = NONE)
class CategoryServiceTest(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("카테고리를 조회할 때") {
        categoryRepository.save(
            category(
                family = "송사리과",
                species = "고정구피",
            )
        )
        categoryRepository.save(
            category(
                family = "송사리과",
                species = "팬시구피",
            )
        )

        val query = CategoryReadQuery(family = "송사리과")

        When("어과 조건을 입력하면") {
            val speciesResponses = categoryService.readSpecies(query)

            Then("조건에 해당하는 어종 목록이 반환된다") {
                speciesResponses shouldContainExactly listOf(
                    SpeciesResponse("고정구피"),
                    SpeciesResponse("팬시구피")
                )
            }
        }
    }

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
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                )
            )

            Then("입력한 어과에 해당하는 상품들이 반환된다") {
                productsResponse.products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("어과와 어종을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    species = listOf("팬시구피")
                )
            )

            Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                productsResponse.products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                )
            }
        }

        When("어과와 여러 어종을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    species = listOf("팬시구피", "고정구피")
                )
            )

            Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                productsResponse.products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("배송 조건을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    deliveryMethod = SAFETY,
                )
            )

            Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                productsResponse.products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                )
            }
        }

        When("정렬 조건을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    sorter = Sorter.SALE_PRICE_DESC
                )
            )

            Then("상품들이 조건에 맞게 정렬되어 반환된다") {
                productsResponse.products shouldContainExactly listOf(
                    ProductResponse(product3, store.name),
                    ProductResponse(product2, store.name),
                    ProductResponse(product1, store.name),
                )
            }
        }

        When("개수 제한을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    limit = 1,
                )
            )

            Then("해당 개수만큼 반환되며 다음 페이지가 존재하는지 여부를 반환한다") {
                assertSoftly(productsResponse) {
                    it.products shouldContainExactly listOf(
                        ProductResponse(product3, store.name),
                    )
                    it.hasNextPage shouldBe true
                }
            }
        }

        When("조건을 입력하면") {
            val productsResponse = categoryService.readProducts(
                CategoryProductReadQuery(
                    family = "송사리과",
                    limit = 1
                )
            )

            Then("입력한 조건에 해당하는 전체 상품의 개수가 함께 반환된다") {
                assertSoftly(productsResponse) {
                    it.products shouldContainExactly listOf(
                        ProductResponse(product3, store.name),
                    )
                    it.totalProductsCount shouldBe 3
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
