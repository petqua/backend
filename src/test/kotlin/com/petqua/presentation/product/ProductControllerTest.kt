package com.petqua.presentation.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSourceType.HOME_NEW_ENROLLMENT
import com.petqua.domain.product.ProductSourceType.HOME_RECOMMENDED
import com.petqua.domain.product.Sorter.REVIEW_COUNT_DESC
import com.petqua.domain.product.Sorter.SALE_PRICE_ASC
import com.petqua.domain.product.Sorter.SALE_PRICE_DESC
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.recommendation.ProductRecommendationRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductExceptionType.INVALID_SEARCH_WORD
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productKeyword
import com.petqua.test.fixture.productRecommendation
import com.petqua.test.fixture.store
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.restassured.common.mapper.TypeRef
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO
import kotlin.Long.Companion.MIN_VALUE

class ProductControllerTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val recommendationRepository: ProductRecommendationRepository,
    private val productKeywordRepository: ProductKeywordRepository,
) : ApiTestConfig() {

    init {
        val store = storeRepository.save(store())

        Given("개별 상품을 조회할 때") {
            val productId = productRepository.save(product(storeId = store.id)).id

            When("상품 ID를 입력하면") {
                val response = requestReadProductById(
                    productId,
                )

                Then("해당 ID의 상품이 반환된다") {
                    val productDetailResponse = response.`as`(ProductDetailResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productDetailResponse shouldBe ProductDetailResponse(
                        product = product(id = productId, storeId = store.id),
                        storeName = store.name,
                        reviewAverageScore = 0.0
                    )
                }
            }

            When("존재하지 않는 상품 ID를 입력하면") {
                val response = requestReadProductById(
                    MIN_VALUE,
                )

                Then("예외가 발생한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe NOT_FOUND.value()
                    exceptionResponse.message shouldBe NOT_FOUND_PRODUCT.errorMessage()
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
            recommendationRepository.save(productRecommendation(productId = product2.id))

            When("마지막으로 조회한 Id를 입력하면") {
                val response = requestReadAllProducts(
                    lastViewedId = product4.id,
                )

                Then("해당 ID의 다음 상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 4
                    )
                }
            }

            When("개수 제한을 입력하면") {
                val response = requestReadAllProducts(
                    limit = 1,
                )

                Then("해당 개수와 함께 다음 페이지가 존재하는지 여부가 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(ProductResponse(product4, store.name)),
                        hasNextPage = true,
                        totalProductsCount = 4
                    )
                }
            }

            When("추천 조건으로 조회하면") {
                val response = requestReadAllProducts(
                    sourceType = HOME_RECOMMENDED.name,
                )

                Then("추천 상품들이, 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 2
                    )
                }
            }

            When("추천 조건으로, 가격 낮은 순으로 조회하면") {
                val response = requestReadAllProducts(
                    sourceType = HOME_RECOMMENDED.name,
                    sorter = SALE_PRICE_ASC.name,
                )

                Then("추천 상품들이, 가격 낮은 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product1, store.name),
                            ProductResponse(product2, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 2
                    )
                }
            }

            When("신규 입고 조건으로 조회하면") {
                val response = requestReadAllProducts(
                    sourceType = HOME_NEW_ENROLLMENT.name,
                )

                Then("신규 입고 상품들이 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product4, store.name),
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 4
                    )
                }
            }

            When("신규 입고 조건으로, 가격 높은 순으로 조회하면") {
                val response = requestReadAllProducts(
                    sourceType = HOME_NEW_ENROLLMENT.name,
                    sorter = SALE_PRICE_DESC.name,
                )

                Then("상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product4, store.name),
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 4
                    )
                }
            }

            When("조건을 잘못 기입해서 조회하면") {
                val response = requestReadAllProducts(
                    sourceType = "WRONG_TYPE",
                )

                Then("예외가 발생한다") {
                    response.statusCode shouldBe BAD_REQUEST.value()
                }
            }
        }

        Given("상품 검색창에서 추천 검색어 기능을 이용할 때") {
            val product1 = productRepository.save(
                product(
                    name = "블루네온 구피",
                    storeId = store.id,
                )
            )
            val product2 = productRepository.save(
                product(
                    name = "레드턱시도 구피",
                    storeId = store.id,
                )
            )
            val product3 = productRepository.save(
                product(
                    name = "열대어 브론디 턱시도 구피",
                    storeId = store.id,
                )
            )
            val product4 = productRepository.save(
                product(
                    name = "단정 금붕어",
                    storeId = store.id,
                )
            )

            productKeywordRepository.save(
                productKeyword(
                    productId = product1.id,
                    word = "구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product1.id,
                    word = "열대어구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product2.id,
                    word = "애완구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product3.id,
                    word = "관상용구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product4.id,
                    word = "금붕어"
                )
            )

            When("검색어를 입력하면") {
                val response = requestReadProductKeyword(
                    word = "구피",
                )

                Then("상품 키워드 목록이 문자 길이 오름차순으로 반환된다") {
                    val productKeywordResponses = response.`as`(object : TypeRef<List<ProductKeywordResponse>>() {})

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productKeywordResponses shouldContainExactly listOf(
                        ProductKeywordResponse("구피"),
                        ProductKeywordResponse("애완구피"),
                        ProductKeywordResponse("관상용구피"),
                        ProductKeywordResponse("열대어구피"),
                    )
                }
            }

            When("검색어를 입력하지 않으면") {
                val response = requestReadProductKeyword(
                    word = null,
                )

                Then("예외가 발생한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    exceptionResponse.message shouldContain "Parameter specified as non-null is null"
                }
            }

            When("검색어를 빈 문자로 입력하면") {
                val response = requestReadProductKeyword(
                    word = " ",
                )

                Then("예외가 발생한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    exceptionResponse.message shouldBe INVALID_SEARCH_WORD.errorMessage()
                }
            }
        }

        Given("검색으로 상품을 조회할 때") {
            val product1 = productRepository.save(
                product(
                    name = "블루네온 구피",
                    storeId = store.id,
                    reviewCount = 2,
                    canDeliverySafely = false,
                    canDeliveryCommonly = false,
                    canPickUp = true,
                )
            )
            val product2 = productRepository.save(
                product(
                    name = "레드턱시도 구피",
                    storeId = store.id,
                    reviewCount = 3,
                    canDeliverySafely = false,
                    canDeliveryCommonly = true,
                    canPickUp = true,
                )
            )
            val product3 = productRepository.save(
                product(
                    name = "열대어 브론디 턱시도 구피",
                    storeId = store.id,
                    reviewCount = 1,
                    canDeliverySafely = true,
                    canDeliveryCommonly = false,
                    canPickUp = true,
                )
            )
            val product4 = productRepository.save(
                product(
                    name = "단정 금붕어",
                    storeId = store.id,
                    reviewCount = 0,
                    canDeliverySafely = true,
                    canDeliveryCommonly = true,
                    canPickUp = true,
                )
            )

            productKeywordRepository.save(
                productKeyword(
                    productId = product1.id,
                    word = "구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product1.id,
                    word = "열대어"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product2.id,
                    word = "구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product3.id,
                    word = "구피"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product4.id,
                    word = "금붕어"
                )
            )
            productKeywordRepository.save(
                productKeyword(
                    productId = product4.id,
                    word = "열대어"
                )
            )

            When("검색어가 상품 키워드에 속하면") {
                val keyword = "열대어"

                val response = requestReadProductBySearch(
                    word = keyword,
                )

                Then("상품 키워드와 연관된 상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product4, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 2
                    )
                }
            }

            When("상품 키워드에 속하는 검색어와 배송 조건을 입력하면") {
                val keyword = "열대어"

                val response = requestReadProductBySearch(
                    word = keyword,
                    deliveryMethod = COMMON
                )

                Then("입력한 조건에 해당하는 상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse.products shouldContainExactly listOf(
                        ProductResponse(product4, store.name),
                    )
                }
            }

            When("상품 키워드에 속하는 검색어와 정렬 조건을 입력하면") {
                val keyword = "열대어"

                val response = requestReadProductBySearch(
                    word = keyword,
                    sorter = REVIEW_COUNT_DESC.name,
                )

                Then("입력한 조건에 해당하는 상품들이 정렬 조건순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse.products shouldContainExactly listOf(
                        ProductResponse(product1, store.name),
                        ProductResponse(product4, store.name),
                    )
                }
            }

            When("검색어가 상품 키워드에 속하지 않으면") {
                val nonKeyword = "구"

                val response = requestReadProductBySearch(
                    word = nonKeyword,
                )

                Then("상품 이름과 연관된 상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse shouldBe ProductsResponse(
                        products = listOf(
                            ProductResponse(product3, store.name),
                            ProductResponse(product2, store.name),
                            ProductResponse(product1, store.name)
                        ),
                        hasNextPage = false,
                        totalProductsCount = 3
                    )
                }
            }

            When("상품 키워드에 속하지 않는 검색어와 배송 조건을 입력하면") {
                val nonKeyword = "구"

                val response = requestReadProductBySearch(
                    word = nonKeyword,
                    deliveryMethod = SAFETY
                )

                Then("상품 이름과 연관된 상품들 중 조건에 해당하는 상품들이 최신 등록 순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse.products shouldContainExactly listOf(
                        ProductResponse(product3, store.name),
                    )
                }
            }

            When("상품 키워드에 속하지 않는 검색어와 정렬 조건을 입력하면") {
                val nonKeyword = "구"

                val response = requestReadProductBySearch(
                    word = nonKeyword,
                    sorter = REVIEW_COUNT_DESC.name
                )

                Then("상품 이름과 연관된 상품들이 정렬 조건순으로 반환된다") {
                    val productsResponse = response.`as`(ProductsResponse::class.java)

                    response.statusCode shouldBe HttpStatus.OK.value()
                    productsResponse.products shouldContainExactly listOf(
                        ProductResponse(product2, store.name),
                        ProductResponse(product1, store.name),
                        ProductResponse(product3, store.name),
                    )
                }
            }

            When("검색어를 입력하지 않으면") {
                val response = requestReadProductBySearch(
                    word = null,
                )

                Then("예외가 발생한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    exceptionResponse.message shouldContain "Parameter specified as non-null is null"

                }
            }

            When("검색어를 빈 문자로 입력하면") {
                val response = requestReadProductBySearch(
                    word = " ",
                )

                Then("예외가 발생한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    response.statusCode shouldBe BAD_REQUEST.value()
                    exceptionResponse.message shouldBe INVALID_SEARCH_WORD.errorMessage()
                }
            }
        }
    }
}
