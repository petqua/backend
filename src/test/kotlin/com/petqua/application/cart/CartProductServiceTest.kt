package com.petqua.application.cart

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.cart.DeliveryMethod
import com.petqua.domain.product.ProductRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.product
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CartProductServiceTest(
    private val cartProductService: CartProductService,
    private val cartProductRepository: CartProductRepository,
    private val productRepository: ProductRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("봉달 상품 저장 명령으로") {
        val productId = productRepository.save(product(id = 1L)).id
        val memberId = 1L
        val command = SaveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = 1,
            isMale = true,
            deliveryMethod = DeliveryMethod.COMMON,
        )

        When("봉달 상품을") {
            cartProductService.save(command)

            Then("저장할 수 있다") {
                cartProductRepository.findAll().size shouldBe 1
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
