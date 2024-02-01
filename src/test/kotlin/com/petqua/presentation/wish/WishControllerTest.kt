package com.petqua.presentation.wish

import com.petqua.domain.product.ProductRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.domain.wish.Wish
import com.petqua.domain.wish.WishRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import com.petqua.test.fixture.wish
import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import java.math.BigDecimal

class WishControllerTest(
    wishRepository: WishRepository,
    productRepository: ProductRepository,
    storeRepository: StoreRepository,
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
                    post("/wishes")
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
                    delete("/wishes/1")
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

        Given("찜 목록 조회를") {
            val memberAuthResponse = signInAsMember()
            val store = storeRepository.save(store())
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
            wishRepository.save(
                Wish(
                    productId = product1.id,
                    memberId = getMemberIdFromAuthResponse(memberAuthResponse)
                )
            )
            wishRepository.save(
                Wish(
                    productId = product2.id,
                    memberId = getMemberIdFromAuthResponse(memberAuthResponse)
                )
            )
            wishRepository.save(
                Wish(
                    productId = product3.id,
                    memberId = getMemberIdFromAuthResponse(memberAuthResponse)
                )
            )

            When("요청하면") {
                val responses = Given {
                    log().all()
                        .header(HttpHeaders.AUTHORIZATION, memberAuthResponse.accessToken)
                } When {
                    get("/wishes")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("찜 상품들이 반환된다") {
                    val wishResponses = responses.`as`(Array<WishResponse>::class.java)

                    responses.statusCode shouldBe OK.value()
                    wishResponses.size shouldBe 3
                }
            }
        }
    }
}
