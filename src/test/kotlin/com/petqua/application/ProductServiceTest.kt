package com.petqua.application

import com.petqua.domain.ProductRepository
import com.petqua.domain.ProductSourceType.NONE
import com.petqua.domain.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.StoreRepository
import com.petqua.dto.ProductDetailResponse
import com.petqua.dto.ProductReadRequest
import com.petqua.dto.ProductResponse
import com.petqua.dto.ProductsResponse
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductServiceTest(
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
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

        val request = ProductReadRequest(
            sourceType = NONE,
            sorter = ENROLLMENT_DATE_DESC,
            limit = 2
        )

        When("상품을") {
            val productsResponse = productService.readAll(request)

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

    afterContainer {
        dataCleaner.clean()
    }
})
