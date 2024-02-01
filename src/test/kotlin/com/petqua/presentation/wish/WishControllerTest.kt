package com.petqua.presentation.wish

import com.petqua.domain.product.ProductRepository
import com.petqua.domain.wish.WishRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import com.petqua.test.fixture.wish
import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType

class WishControllerTest(
    productRepository: ProductRepository,
    wishRepository: WishRepository
) : ApiTestConfig() {

    init {
        Given("찜 추가를") {
            val memberAuthResponse = signInAsMember()
            val product = productRepository.save(product())
            val request = SaveWishRequest(
                productId = product.id
            )

            When("요청하면") {
                val response = Given {
                    log().all()
                        .body(request)
                        .header(HttpHeaders.AUTHORIZATION, memberAuthResponse.accessToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                } When {
                    post("/wish")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("찜 목록에 상품이 추가된다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }

        Given("찜 삭제를") {
            val memberAuthResponse = signInAsMember()
            val product = productRepository.save(
                product(
                    wishCount = 1
                )
            )
            val wish = wishRepository.save(wish())

            When("요청하면") {
                val response = Given {
                    log().all()
                        .header(HttpHeaders.AUTHORIZATION, memberAuthResponse.accessToken)
                } When {
                    delete("/wish/1")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("찜 목록에서 상품이 삭제된다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }
    }
}
