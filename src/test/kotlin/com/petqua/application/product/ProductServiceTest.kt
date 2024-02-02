package com.petqua.application.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productKeyword
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ProductServiceTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productKeywordRepository: ProductKeywordRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "store"))

    Given("상품 ID로") {
        val productId = productRepository.save(product(storeId = store.id)).id

        When("상품을") {
            val productDetailResponse = productService.readById(productId)

            Then("조회할 수 있다") {
                productDetailResponse shouldBe ProductDetailResponse(
                    product = product(id = productId, storeId = store.id),
                    storeName = "store",
                    0.0
                )
            }
        }
    }

    Given("조건에 따라") {
        val product1 = productRepository.save(product(storeId = store.id))
        val product2 = productRepository.save(product(storeId = store.id))

        val query = ProductReadQuery(
            sourceType = NONE,
            sorter = ENROLLMENT_DATE_DESC,
            limit = 2
        )

        When("상품을") {
            val productsResponse = productService.readAll(query)

            Then("조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product2, store.name),
                        ProductResponse(product1, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }
    }

    Given("상품 검색창에서 추천 검색어 기능을 이용할 때") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))
        val product2 = productRepository.save(product(name = "레드턱시도 구피", storeId = store.id))
        val product3 = productRepository.save(product(name = "고등어", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "구피", productId = product2.id))
        productKeywordRepository.save(productKeyword(word = "레드턱시도 구피", productId = product2.id))

        val query = ProductKeywordQuery(word = "구피")

        When("검색어를 입력하면") {
            val productKeywordResponses = productService.readKeywords(query)

            Then("상품 키워드 목록이 문자 길이 오름차순으로 반환된다") {
                productKeywordResponses shouldBe listOf(
                    ProductKeywordResponse("구피"),
                    ProductKeywordResponse("블루네온 구피"),
                    ProductKeywordResponse("레드턱시도 구피"),
                )
            }
        }
    }

    Given("검색으로 상품을 조회할 때") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))
        val product2 = productRepository.save(product(name = "레드턱시도 구피", storeId = store.id))
        val product3 = productRepository.save(product(name = "고등어", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "구피", productId = product2.id))
        productKeywordRepository.save(productKeyword(word = "레드턱시도 구피", productId = product2.id))

        When("검색어가 상품 키워드에 속하면") {
            val query = ProductSearchQuery(word = "구피")

            val productsResponse = productService.readBySearch(query)

            Then("상품 키워드와 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product2, store.name),
                        ProductResponse(product1, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 2
                )
            }
        }

        When("검색어가 상품 키워드에 속하지 않으면") {
            val query = ProductSearchQuery(word = "고등")

            val productsResponse = productService.readBySearch(query)

            Then("상품 이름과 연관된 상품을 조회할 수 있다") {
                productsResponse shouldBe ProductsResponse(
                    products = listOf(
                        ProductResponse(product3, store.name),
                    ),
                    hasNextPage = false,
                    totalProductsCount = 1
                )
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
