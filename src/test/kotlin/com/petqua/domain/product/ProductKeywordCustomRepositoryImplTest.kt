package com.petqua.domain.product

import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productKeyword
import com.petqua.test.fixture.store
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductKeywordCustomRepositoryImplTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productKeywordRepository: ProductKeywordRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    val store = storeRepository.save(store(name = "펫쿠아"))

    Given("검색을 통해") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))
        val product2 = productRepository.save(product(name = "레드턱시도 구피", storeId = store.id))
        val product3 = productRepository.save(product(name = "고등어", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "구피", productId = product2.id))
        productKeywordRepository.save(productKeyword(word = "레드턱시도 구피", productId = product2.id))

        val word = "구피"
        val limit = 5

        When("상품 키워드 목록을") {
            val productKeywordResponses = productKeywordRepository.findBySearch(word, limit)

            Then("조회할 수 있다") {
                productKeywordResponses shouldBe listOf(
                    ProductKeywordResponse("구피"),
                    ProductKeywordResponse("블루네온 구피"),
                    ProductKeywordResponse("레드턱시도 구피"),
                )
            }
        }
    }

    Given("검색어를 입력해 상품 키워드 목록에 있는지 확인할 때") {
        val product1 = productRepository.save(product(name = "블루네온 구피", storeId = store.id))

        productKeywordRepository.save(productKeyword(word = "구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "열대어구피", productId = product1.id))
        productKeywordRepository.save(productKeyword(word = "블루네온 구피", productId = product1.id))


        When("상품 키워드 목록에 없는 검색어를 입력하면") {
            val actual = productKeywordRepository.existsByWord("금붕어")

            Then("false 를 반환한다") {
                actual shouldBe false
            }
        }

        When("상품 키워드 목록에 있는 검색어를 입력하면") {
            val actual = productKeywordRepository.existsByWord("구피")

            Then("true 를 반환한다") {
                actual shouldBe true
            }
        }
    }
})
