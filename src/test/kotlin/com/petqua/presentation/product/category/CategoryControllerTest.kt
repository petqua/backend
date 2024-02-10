package com.petqua.presentation.product.category

import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.StoreRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.category
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.restassured.common.mapper.TypeRef
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import java.math.BigDecimal

class CategoryControllerTest(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
) : ApiTestConfig() {

    init {

        Given("어과 카테고리를 통해 어종을 조회할 때") {
            categoryRepository.save(
                category(
                    family = "송사리과",
                    species = "고정구피",
                )
            )

            When("어과를 입력하면") {
                val response = requestReadSpecies(family = "송사리과")

                Then("입력한 어과에 해당하는 어종들이 반환된다") {
                    val speciesResponses = response.`as`(object : TypeRef<List<SpeciesResponse>>() {})

                    response.statusCode shouldBe OK.value()
                    speciesResponses shouldContainExactly listOf(
                        SpeciesResponse("고정구피")
                    )
                }
            }

            When("어과를 입력하지 않으면") {
                val response = requestReadSpecies(family = null)

                Then("예외가 반환된다") {
                    response.statusCode shouldBe BAD_REQUEST.value()
                }
            }
        }

        Given("어과 카테고리를 통해 상품을 조회할 때") {
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
                val response = requestReadProducts(family = "송사리과")

                Then("입력한 어과에 해당하는 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name),
                        )
                    }
                }
            }

            When("어과와 어종을 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    species = listOf("팬시구피")
                )

                Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                        )
                    }
                }
            }

            When("어과와 여러 어종을 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    species = listOf("팬시구피", "고정구피")
                )

                Then("입력한 어과와 어종에 해당하는 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name),
                        )
                    }
                }
            }


            When("배송 조건을 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    canDeliverSafely = true
                )

                Then("입력한 배송 조건에 해당하는 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product3, store.name),
                        )
                    }
                }
            }

            When("개수 제한을 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    limit = 1
                )

                Then("해당 개수만큼의 상품과 다음 페이지가 존재하는지 여부가 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product3, store.name),
                        )
                        productsResponse.hasNextPage shouldBe true
                    }
                }
            }

            When("마지막으로 조회한 상품의 id를 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    lastViewedId = product3.id,
                )

                Then("해당 상품 이후의 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name),
                        )
                    }
                }
            }

            When("정렬 조건을 입력하면") {
                val response = requestReadProducts(
                    family = "송사리과",
                    species = listOf("팬시구피"),
                    sorter = SALE_PRICE_ASC,
                )

                Then("해당 조건으로 정렬한 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    assertSoftly {
                        response.statusCode shouldBe OK.value()
                        productsResponse.products shouldContainExactly listOf(
                            ProductResponse(product2, store.name),
                            ProductResponse(product3, store.name),
                        )
                    }
                }
            }

            When("어과를 입력하지 않으면") {
                val response = requestReadProducts(family = null)

                Then("예외가 반환된다") {
                    response.statusCode shouldBe BAD_REQUEST.value()
                }
            }
        }
    }
}
