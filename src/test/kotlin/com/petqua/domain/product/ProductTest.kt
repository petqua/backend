package com.petqua.domain.product

import com.petqua.domain.order.OrderNumber
import com.petqua.test.fixture.product
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ProductTest : BehaviorSpec({

    Given("상품의") {
        val product = product(
            reviewCount = 3,
            reviewTotalScore = 13
        )

        When("평균 리뷰 점수를 계산하면") {
            val average = product.averageReviewScore()

            Then("소수점 두 번째 자리에서 반올림 된다") {
                average shouldBe 4.3
            }
        }
    }


}) {
    @Test
    fun test() {
        println(OrderNumber.generate().value)
    }
}
