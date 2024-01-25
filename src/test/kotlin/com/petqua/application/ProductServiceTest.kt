package com.petqua.application

import com.petqua.domain.ProductRepository
import com.petqua.domain.StoreRepository
import com.petqua.dto.ProductDetailResponse
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductServiceTest(
    @Autowired
    private val productService: ProductService,

    @Autowired
    private val productRepository: ProductRepository,

    @Autowired
    private val storeRepository: StoreRepository,
) : BehaviorSpec({

    val storeId = storeRepository.save(store(name = "store")).id

    Given("상품 ID로") {
        val productId = productRepository.save(product(storeId = storeId)).id

        When("상품을") {
            val productDetailResponse = productService.readById(productId)

            Then("조회할 수 있다") {
                productDetailResponse shouldBe ProductDetailResponse(
                    product = product(id = productId, storeId = storeId),
                    storeName = "store",
                    0.0
                )
            }
        }
    }
})
