package com.petqua.domain

import com.petqua.application.ProductReadConditions
import com.petqua.test.fixture.product
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductCustomRepositoryImplTest(
        @Autowired private val productRepository: ProductRepository,
) : BehaviorSpec({

    Given("조건에 따라 상품을 조회할 때") {
        productRepository.save(product(name = "상품1"))
        productRepository.save(product(name = "상품2"))
        productRepository.save(product(name = "상품3"))

        When("개수 제한을 입력하면") {
            val products: List<Product> = productRepository.findAllByConditions(ProductReadConditions(limit = 2))

            Then("해당 개수만큼 반환한다") {
                products shouldHaveSize 2
            }
        }
    }
})
