package com.petqua.presentation.cart

import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.product.ProductRepository
import com.petqua.exception.cart.CartProductExceptionType.INVALID_DELIVERY_METHOD
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.presentation.cart.dto.SaveCartProductRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

private const val AUTHORIZATION_HEADER = "X-MEMBER-ID" // FIXME: 인가 구현 완료 후 변경 예정

class CartProductControllerTest(
    private val productRepository: ProductRepository
) : ApiTestConfig() {
    init {
        val savedProduct = productRepository.save(product(id = 1L))
        Given("봉달에 상품 저장을") {
            val request = SaveCartProductRequest(
                productId = savedProduct.id,
                quantity = 1,
                isMale = true,
                deliveryMethod = "SAFETY"
            )
            When("요청 하면") {
                val response = Given {
                    log().all()
                        .body(request)
                        .header(AUTHORIZATION_HEADER, 1L)
                        .contentType("application/json")
                } When {
                    post("/carts")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("배너 목록을 응답한다.") {
                    assertSoftly {
                        it.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED.value())
                        it.assertThat(response.header(HttpHeaders.LOCATION)).contains("/carts/items")
                    }
                }
            }
        }

        Given("봉달에 상품 저장 요청시") {
            When("지원하지 않는 배송 방식으로 요청 하면") {
                val request = SaveCartProductRequest(
                    productId = savedProduct.id,
                    quantity = 1,
                    isMale = true,
                    deliveryMethod = "NOT_SUPPORTED"
                )
                val response = Given {
                    log().all()
                        .body(request)
                        .header(AUTHORIZATION_HEADER, 1L)
                        .contentType("application/json")
                } When {
                    post("/carts")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly {
                        it.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value())
                        it.assertThat(errorResponse.message).isEqualTo(INVALID_DELIVERY_METHOD.errorMessage())
                    }
                }
            }

            When("존재 하지 않는 상품 저장을 요청 하면") {
                val request = SaveCartProductRequest(
                    productId = 999L,
                    quantity = 1,
                    isMale = true,
                    deliveryMethod = "SAFETY"
                )
                val response = Given {
                    log().all()
                        .body(request)
                        .header(AUTHORIZATION_HEADER, 1L)
                        .contentType("application/json")
                } When {
                    post("/carts")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly {
                        it.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value())
                        it.assertThat(errorResponse.message).isEqualTo(NOT_FOUND_PRODUCT.errorMessage())
                    }
                }
            }
        }
    }
}
