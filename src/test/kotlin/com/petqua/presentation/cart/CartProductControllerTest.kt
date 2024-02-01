package com.petqua.presentation.cart

import com.petqua.application.cart.dto.CartProductResponse
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.cart.CartProductExceptionType.DUPLICATED_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.FORBIDDEN_CART_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.INVALID_DELIVERY_METHOD
import com.petqua.exception.cart.CartProductExceptionType.NOT_FOUND_CART_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_OVER_MAXIMUM
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.presentation.cart.dto.SaveCartProductRequest
import com.petqua.presentation.cart.dto.UpdateCartProductOptionRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.product
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT

class CartProductControllerTest(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
) : ApiTestConfig() {
    init {
        val storeId = storeRepository.save(store()).id
        Given("봉달에 상품 저장을") {
            val memberAuthResponse = signInAsMember()
            val savedProduct = productRepository.save(product(storeId = storeId))
            val request = SaveCartProductRequest(
                productId = savedProduct.id,
                quantity = 1,
                isMale = true,
                deliveryMethod = "SAFETY"
            )
            When("요청 하면") {
                val response = requestSaveCartProduct(request, memberAuthResponse.accessToken)

                Then("봉달 목록에 상품이 저장된다") {
                    assertSoftly(response) {
                        statusCode shouldBe CREATED.value()
                        header(HttpHeaders.LOCATION) shouldContain "/carts/items/1"
                    }
                }
            }
        }

        Given("봉달에 상품 저장 요청시") {
            val memberAuthResponse = signInAsMember()
            val savedProduct = productRepository.save(product(storeId = storeId))
            When("지원하지 않는 배송 방식으로 요청 하면") {
                val invalidDeliveryMethodRequest = SaveCartProductRequest(
                    productId = savedProduct.id,
                    quantity = 1,
                    isMale = true,
                    deliveryMethod = "NOT_SUPPORTED"
                )
                val response = requestSaveCartProduct(invalidDeliveryMethodRequest, memberAuthResponse.accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe INVALID_DELIVERY_METHOD.errorMessage()
                    }
                }
            }

            When("존재 하지 않는 상품 저장을 요청 하면") {
                val notExistProductRequest = SaveCartProductRequest(
                    productId = 999L,
                    quantity = 1,
                    isMale = true,
                    deliveryMethod = "SAFETY"
                )
                val response = requestSaveCartProduct(notExistProductRequest, memberAuthResponse.accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe NOT_FOUND.value()
                        errorResponse.message shouldBe NOT_FOUND_PRODUCT.errorMessage()
                    }
                }
            }

            When("유효하지 않은 상품 수량을 담으면") {
                val invalidProductQuantityRequest = SaveCartProductRequest(
                    productId = savedProduct.id,
                    quantity = 1_000,
                    isMale = false,
                    deliveryMethod = "SAFETY"
                )
                val response = requestSaveCartProduct(invalidProductQuantityRequest, memberAuthResponse.accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe PRODUCT_QUANTITY_OVER_MAXIMUM.errorMessage()
                    }
                }
            }

            When("중복 상품을 담으면") {
                val request = SaveCartProductRequest(
                    productId = savedProduct.id,
                    quantity = 1,
                    isMale = true,
                    deliveryMethod = "SAFETY"
                )
                requestSaveCartProduct(request, memberAuthResponse.accessToken)

                val response = requestSaveCartProduct(request, memberAuthResponse.accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe DUPLICATED_PRODUCT.errorMessage()
                    }
                }
            }
        }

        Given("봉달 상품의 옵션 수정을") {
            val savedProduct = productRepository.save(product(storeId = storeId))
            val memberAuthResponse = signInAsMember()
            val cartProductId = saveCartProductAndReturnId(memberAuthResponse.accessToken, savedProduct.id)

            When("요청 하면") {
                val request = UpdateCartProductOptionRequest(
                    quantity = 2,
                    isMale = false,
                    deliveryMethod = "SAFETY"
                )

                val response = requestUpdateCartProductOption(
                    cartProductId,
                    request,
                    memberAuthResponse.accessToken
                )

                Then("봉달 상품의 옵션이 수정된다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }

        Given("봉달 상품의 옵션 수정시") {
            val savedProduct = productRepository.save(product(storeId = storeId))
            val memberAuthResponse = signInAsMember()
            val cartProductId = saveCartProductAndReturnId(memberAuthResponse.accessToken, savedProduct.id)

            When("존재하지 않는 봉달 상품 수정을 요청 하면") {
                val request = UpdateCartProductOptionRequest(
                    quantity = 2,
                    isMale = false,
                    deliveryMethod = "SAFETY"
                )

                val response = requestUpdateCartProductOption(
                    999L,
                    request,
                    memberAuthResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe NOT_FOUND.value()
                        errorResponse.message shouldBe NOT_FOUND_CART_PRODUCT.errorMessage()
                    }
                }
            }

            When("유효하지 않은 상품 수량으로 수정 하면") {
                val request = UpdateCartProductOptionRequest(
                    quantity = 1_000,
                    isMale = false,
                    deliveryMethod = "SAFETY"
                )

                val response = requestUpdateCartProductOption(
                    cartProductId,
                    request,
                    memberAuthResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe PRODUCT_QUANTITY_OVER_MAXIMUM.errorMessage()
                    }
                }
            }

            When("지원하지 않는 배송 방식으로 수정 하면") {
                val request = UpdateCartProductOptionRequest(
                    quantity = 2,
                    isMale = false,
                    deliveryMethod = "NOT_SUPPORTED"
                )

                val response = requestUpdateCartProductOption(
                    cartProductId,
                    request,
                    memberAuthResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe INVALID_DELIVERY_METHOD.errorMessage()
                    }
                }
            }

            When("다른 회원의 상품을 수정 하면") {
                val otherMemberResponse = signInAsMember()
                val request = UpdateCartProductOptionRequest(
                    quantity = 2,
                    isMale = false,
                    deliveryMethod = "SAFETY"
                )

                val response = requestUpdateCartProductOption(
                    cartProductId,
                    request,
                    otherMemberResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe FORBIDDEN.value()
                        errorResponse.message shouldBe FORBIDDEN_CART_PRODUCT.errorMessage()
                    }
                }
            }

            When("중복 상품을 수정 하면") {
                requestSaveCartProduct(
                    SaveCartProductRequest(
                        productId = savedProduct.id,
                        quantity = 1,
                        isMale = true,
                        deliveryMethod = "SAFETY"
                    ),
                    memberAuthResponse.accessToken
                )
                val duplicationProductOptionRequest = UpdateCartProductOptionRequest(
                    quantity = 2,
                    isMale = true,
                    deliveryMethod = "SAFETY"
                )

                val response = requestUpdateCartProductOption(
                    cartProductId,
                    duplicationProductOptionRequest,
                    memberAuthResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe DUPLICATED_PRODUCT.errorMessage()
                    }
                }
            }
        }

        Given("봉달 상품 삭제를") {
            val product = productRepository.save(product(storeId = storeId))
            val memberAuthResponse = signInAsMember()
            val cartProductAId = saveCartProductAndReturnId(memberAuthResponse.accessToken, product.id)


            When("요청 하면") {
                val response = requestDeleteCartProduct(
                    cartProductAId,
                    memberAuthResponse.accessToken
                )

                Then("봉달 상품이 삭제된다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }
            }
        }

        Given("봉달 상품 삭제시") {
            val product = productRepository.save(product(storeId = storeId))
            val memberAuthResponse = signInAsMember()
            val cartProductAId = saveCartProductAndReturnId(memberAuthResponse.accessToken, product.id)

            When("존재하지 않는 봉달 상품 삭제를 요청 하면") {
                val response = requestDeleteCartProduct(
                    Long.MIN_VALUE,
                    memberAuthResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe NOT_FOUND.value()
                        errorResponse.message shouldBe NOT_FOUND_CART_PRODUCT.errorMessage()
                    }
                }
            }

            When("다른 회원의 상품을 삭제 하면") {
                val otherMemberResponse = signInAsMember()
                val response = requestDeleteCartProduct(
                    cartProductAId,
                    otherMemberResponse.accessToken
                )

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe FORBIDDEN.value()
                        errorResponse.message shouldBe FORBIDDEN_CART_PRODUCT.errorMessage()
                    }
                }
            }
        }

        Given("봉달 목록 전체 조회를") {
            val productA = productRepository.save(product(name = "쿠아1", storeId = storeId))
            val productB = productRepository.save(product(name = "쿠아2", storeId = storeId))
            val productC = productRepository.save(product(name = "쿠아3", storeId = storeId))
            val memberAuthResponse = signInAsMember()
            saveCartProductAndReturnId(memberAuthResponse.accessToken, productA.id)
            saveCartProductAndReturnId(memberAuthResponse.accessToken, productB.id)
            saveCartProductAndReturnId(memberAuthResponse.accessToken, productC.id)

            When("요청 하면") {
                val response = requestReadAllCartProducts(memberAuthResponse.accessToken)

                Then("봉달 목록이 조회된다") {
                    val responseBody = response.`as`(Array<CartProductResponse>::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        responseBody.size shouldBe 3
                        responseBody.map { it.productName }.toList() shouldContainAll listOf("쿠아1", "쿠아2", "쿠아3")
                    }
                }
            }

            When("봉달 목록이 없으면") {
                val otherMemberResponse = signInAsMember()
                val response = requestReadAllCartProducts(otherMemberResponse.accessToken)

                Then("빈 목록이 조회된다") {
                    val responseBody = response.`as`(Array<CartProductResponse>::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        responseBody shouldBe emptyArray<CartProductResponse>()
                    }
                }
            }

            When("봉달에 담은 상품이 삭제 되면") {
                productRepository.delete(productA)
                val response = requestReadAllCartProducts(memberAuthResponse.accessToken)

                Then("삭제된 상품은 구매 불가능 하도록 조회된다") {
                    val responseBody = response.`as`(Array<CartProductResponse>::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        responseBody.size shouldBe 3
                        responseBody.find { it.productId == productA.id }!!.isOnSale shouldBe false
                    }
                }
            }
        }
    }
}
