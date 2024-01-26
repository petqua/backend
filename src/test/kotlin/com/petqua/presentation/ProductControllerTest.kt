package com.petqua.presentation

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSourceType.HOME_NEW_ENROLLMENT
import com.petqua.domain.product.ProductSourceType.HOME_RECOMMENDED
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.Sorter.SALE_PRICE_DESC
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productRecommendation
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import java.math.BigDecimal
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerTest(
    @LocalServerPort private val port: Int,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    RestAssured.port = port
    val store = storeRepository.save(store())

    Given("개별 상품을 조회할 때") {
        val productId = productRepository.save(product(storeId = store.id)).id

        When("상품 ID를 입력하면") {
            val response = Given {
                log().all()
                pathParam("productId", productId)
            } When {
                get("/products/{productId}")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("해당 ID의 상품이 반환된다") {
                val productDetailResponse = response.`as`(ProductDetailResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productDetailResponse).isEqualTo(
                        ProductDetailResponse(
                            product = product(id = productId, storeId = store.id),
                            storeName = store.name,
                            reviewAverageScore = 0.0
                        )
                    )
                }
            }
        }

        When("존재하지 않는 상품 ID를 입력하면") {

            Then("예외가 발생한다") {
                Given {
                    log().all()
                    pathParam("productId", MIN_VALUE)
                } When {
                    get("/products/{productId}")
                } Then {
                    log().all()
                    statusCode(NOT_FOUND.value())
                }
            }
        }
    }

    Given("조건에 따라 상품을 조회할 때") {
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
        val product4 = productRepository.save(
            product(
                name = "상품4",
                storeId = store.id,
                discountPrice = BigDecimal.TEN,
                reviewCount = 2,
                reviewTotalScore = 10
            )
        )

        recommendationRepository.save(productRecommendation(productId = product1.id))
        recommendationRepository.save(productRecommendation(productId = product2.id))

        When("마지막으로 조회한 Id를 입력하면") {
            val response = Given {
                log().all()
                param("lastViewedId", product4.id)
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("해당 ID의 다음 상품들이 최신 등록 순으로 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(
                                ProductResponse(product3, store.name),
                                ProductResponse(product2, store.name),
                                ProductResponse(product1, store.name)
                            ),
                            hasNextPage = false,
                            totalProductsCount = 4
                        )
                    )
                }
            }
        }

        When("개수 제한을 입력하면") {
            val response = Given {
                log().all()
                param("limit", 1)
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("해당 개수와 함께 다음 페이지가 존재하는지 여부가 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(ProductResponse(product4, store.name)),
                            hasNextPage = true,
                            totalProductsCount = 4
                        )
                    )
                }
            }
        }

        When("추천 조건으로 조회하면") {
            val response = Given {
                log().all()
                param("sourceType", HOME_RECOMMENDED.name)
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("추천 상품들이, 최신 등록 순으로 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(
                                ProductResponse(product2, store.name),
                                ProductResponse(product1, store.name)
                            ),
                            hasNextPage = false,
                            totalProductsCount = 2
                        )
                    )
                }
            }
        }

        When("추천 조건으로, 가격 낮은 순으로 조회하면") {
            val response = Given {
                log().all()
                params(
                    "sourceType", HOME_RECOMMENDED.name,
                    "sorter", SALE_PRICE_ASC.name
                )
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("추천 상품들이, 가격 낮은 순으로 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(
                                ProductResponse(product1, store.name),
                                ProductResponse(product2, store.name)
                            ),
                            hasNextPage = false,
                            totalProductsCount = 2
                        )
                    )
                }
            }
        }

        When("신규 입고 조건으로 조회하면") {
            val response = Given {
                log().all()
                param("sourceType", HOME_NEW_ENROLLMENT.name)
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("신규 입고 상품들이 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(
                                ProductResponse(product4, store.name),
                                ProductResponse(product3, store.name),
                                ProductResponse(product2, store.name),
                                ProductResponse(product1, store.name)
                            ),
                            hasNextPage = false,
                            totalProductsCount = 4
                        )
                    )
                }
            }
        }

        When("신규 입고 조건으로, 가격 높은 순으로 조회하면") {
            val response = Given {
                log().all()
                params(
                    "sourceType", HOME_NEW_ENROLLMENT.name,
                    "sorter", SALE_PRICE_DESC.name
                )
            } When {
                get("/products")
            } Then {
                log().all()
            } Extract {
                response()
            }

            Then("상품들이 최신 등록 순으로 반환된다") {
                val productsResponse = response.`as`(ProductsResponse::class.java)

                assertSoftly {
                    it.assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
                    it.assertThat(productsResponse).isEqualTo(
                        ProductsResponse(
                            products = listOf(
                                ProductResponse(product4, store.name),
                                ProductResponse(product3, store.name),
                                ProductResponse(product2, store.name),
                                ProductResponse(product1, store.name)
                            ),
                            hasNextPage = false,
                            totalProductsCount = 4
                        )
                    )
                }
            }
        }

        When("조건을 잘못 기입해서 조회하면") {

            Then("예외가 발생한다") {
                Given {
                    log().all()
                    param("sourceType", "wrongType")
                } When {
                    get("/products")
                } Then {
                    log().all()
                    statusCode(BAD_REQUEST.value())
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
