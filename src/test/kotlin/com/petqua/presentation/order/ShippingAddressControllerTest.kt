package com.petqua.presentation.order

import com.petqua.application.order.dto.ReadDefaultShippingAddressResponse
import com.petqua.application.order.dto.SaveShippingAddressResponse
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.exception.order.ShippingAddressExceptionType
import com.petqua.presentation.order.dto.SaveShippingAddressRequest
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.shippingAddress
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class ShippingAddressControllerTest(
    private val shippingAddressRepository: ShippingAddressRepository,
) : ApiTestConfig() {

    init {
        Given("배송지 생성을") {
            val accessToken = signInAsMember().accessToken
            val request = SaveShippingAddressRequest(
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-1234-1234",
                zipCode = 12345,
                address = "서울특별시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true,
            )

            When("요청하면") {
                val response = requestSaveShippingAddress(request, accessToken)

                Then("배송지가 생성된다") {
                    val responseBody = response.`as`(SaveShippingAddressResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        shippingAddressRepository.findById(responseBody.id).shouldNotBeNull()
                    }
                }
            }
        }

        Given("배송지 생성 요청시") {
            val accessToken = signInAsMember().accessToken

            When("전화번호 형식이 올바르지 않으면") {
                val request = SaveShippingAddressRequest(
                    name = "집",
                    receiver = "홍길동",
                    phoneNumber = "010-123-1234",
                    zipCode = 12345,
                    address = "서울특별시 강남구 역삼동 99번길",
                    detailAddress = "101동 101호",
                    isDefaultAddress = true,
                )
                val response = requestSaveShippingAddress(request, accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.BAD_REQUEST.value()
                        errorResponse.code shouldBe ShippingAddressExceptionType.INVALID_PHONE_NUMBER.code()
                    }
                }
            }

            When("배송지 이름이 비어있으면") {
                val request = SaveShippingAddressRequest(
                    name = "",
                    receiver = "홍길동",
                    phoneNumber = "010-1234-1234",
                    zipCode = 12345,
                    address = "서울특별시 강남구 역삼동 99번길",
                    detailAddress = "101동 101호",
                    isDefaultAddress = true,
                )
                val response = requestSaveShippingAddress(request, accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.BAD_REQUEST.value()
                        errorResponse.code shouldBe ShippingAddressExceptionType.EMPTY_NAME.code()
                    }
                }
            }

            When("받는 사람이 비어있으면") {
                val request = SaveShippingAddressRequest(
                    name = "집1",
                    receiver = "",
                    phoneNumber = "010-1234-1234",
                    zipCode = 12345,
                    address = "서울특별시 강남구 역삼동 99번길",
                    detailAddress = "101동 101호",
                    isDefaultAddress = true,
                )
                val response = requestSaveShippingAddress(request, accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.BAD_REQUEST.value()
                        errorResponse.code shouldBe ShippingAddressExceptionType.EMPTY_RECEIVER.code()
                    }
                }
            }

            When("주소가 비어있으면") {
                val request = SaveShippingAddressRequest(
                    name = "집",
                    receiver = "홍길동",
                    phoneNumber = "010-1234-1234",
                    zipCode = 12345,
                    address = "",
                    detailAddress = "101동 101호",
                    isDefaultAddress = true,
                )
                val response = requestSaveShippingAddress(request, accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.BAD_REQUEST.value()
                        errorResponse.code shouldBe ShippingAddressExceptionType.EMPTY_ADDRESS.code()
                    }
                }
            }

            When("상세주소가 비어있으면") {
                val request = SaveShippingAddressRequest(
                    name = "집",
                    receiver = "홍길동",
                    phoneNumber = "010-1234-1234",
                    zipCode = 12345,
                    address = "서울특별시 강남구 역삼동 99번길",
                    detailAddress = "",
                    isDefaultAddress = true,
                )
                val response = requestSaveShippingAddress(request, accessToken)

                Then("예외가 발생한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.BAD_REQUEST.value()
                        errorResponse.code shouldBe ShippingAddressExceptionType.EMPTY_DETAIL_ADDRESS.code()
                    }
                }
            }
        }

        Given("기본 배송지 조회를") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)
            val savedShippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                    isDefaultAddress = true
                )
            )

            When("요청하면") {
                val response = requestReadDefaultShippingAddress(accessToken)

                Then("멤버의 기본 배송지가 조회된다") {
                    val responseBody = response.`as`(ReadDefaultShippingAddressResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        responseBody.id shouldBe savedShippingAddress.id
                        responseBody.name shouldBe savedShippingAddress.name
                        responseBody.receiver shouldBe savedShippingAddress.receiver
                        responseBody.phoneNumber shouldBe savedShippingAddress.phoneNumber
                        responseBody.zipCode shouldBe savedShippingAddress.zipCode
                        responseBody.address shouldBe savedShippingAddress.address
                        responseBody.detailAddress shouldBe savedShippingAddress.detailAddress
                        responseBody.isDefaultAddress shouldBe savedShippingAddress.isDefaultAddress
                    }
                }
            }
        }

        Given("기본 배송지 조회시") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)
            shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                    isDefaultAddress = false
                )
            )

            When("멤버가 기본 배송지를 설정하지 않았으면") {
                val response = requestReadDefaultShippingAddress(accessToken)

                Then("빈 응답이 반환된다") {
                    assertSoftly(response) {
                        statusCode shouldBe HttpStatus.OK.value()
                        response.body.asString().isEmpty().shouldBeTrue()
                    }
                }
            }
        }
    }
}
