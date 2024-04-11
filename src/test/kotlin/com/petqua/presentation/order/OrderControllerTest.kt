package com.petqua.presentation.order

import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.common.domain.Money
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.PICK_UP
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.order.findByOrderNumberOrThrow
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.ProductSnapshotRepository
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderExceptionType.ORDER_PRICE_NOT_MATCH
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
import com.petqua.exception.product.ProductExceptionType.INVALID_PRODUCT_OPTION
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.orderProductRequest
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productOption
import com.petqua.test.fixture.saveOrderRequest
import com.petqua.test.fixture.shippingAddress
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus.BAD_REQUEST
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import kotlin.Long.Companion.MIN_VALUE

class OrderControllerTest(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
) : ApiTestConfig() {

    init {
        Given("주문을 할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            val storeA = storeRepository.save(
                store(
                    name = "storeA"
                )
            )
            val storeB = storeRepository.save(
                store(
                    name = "storeB"
                )
            )

            val productA1 = productRepository.save(
                product(
                    storeId = storeA.id,
                    name = "1",
                    pickUpDeliveryFee = ZERO,
                    commonDeliveryFee = 3000.toBigDecimal(),
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            val productA2 = productRepository.save(
                product(
                    storeId = storeA.id,
                    name = "2",
                    pickUpDeliveryFee = ZERO,
                    commonDeliveryFee = 3000.toBigDecimal(),
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            val productB1 = productRepository.save(
                product(
                    storeId = storeB.id,
                    name = "1",
                    pickUpDeliveryFee = ZERO,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )

            val productOptionA1 = productOptionRepository.save(
                productOption(
                    productId = productA1.id,
                    sex = FEMALE,
                )
            )
            val productOptionA2 = productOptionRepository.save(
                productOption(
                    productId = productA2.id,
                    sex = MALE,
                )
            )
            val productOptionB1 = productOptionRepository.save(
                productOption(
                    productId = productB1.id,
                    sex = MALE,
                )
            )

            productSnapshotRepository.save(ProductSnapshot.from(productA1))
            productSnapshotRepository.save(ProductSnapshot.from(productA2))
            productSnapshotRepository.save(ProductSnapshot.from(productB1))

            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId
                )
            )

            When("하나의 상점에서 일반 배송 조건으로 두 개의 상품을 주문한다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = FEMALE,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON
                )
                val productOrderA2 = orderProductRequest(
                    product = productA2,
                    productOption = productOptionA2,
                    quantity = 1,
                    sex = MALE,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                    productOrderA2,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + productA2.discountPrice + Money.from(3000),
                )

                requestSaveOrder(request, accessToken)

                Then("배송번호는 한 개만 생성된다") {
                    val orders = orderRepository.findAll()

                    orders.distinctBy { it.orderProduct.shippingNumber }.size shouldBe 1
                }
            }

            When("하나의 상점에서 여러 배송 조건으로 두 개의 상품을 주문한다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = FEMALE,
                    deliveryFee = Money.from(ZERO),
                    deliveryMethod = PICK_UP
                )
                val productOrderA2 = orderProductRequest(
                    product = productA2,
                    productOption = productOptionA2,
                    quantity = 1,
                    sex = MALE,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                    productOrderA2,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + productA2.discountPrice + Money.from(3000),
                )

                requestSaveOrder(request, accessToken)

                Then("배송번호는 두 개 생성된다") {
                    val orders = orderRepository.findAll()

                    orders.distinctBy { it.orderProduct.shippingNumber }.size shouldBe 2
                }
            }

            When("하나의 상점에서 직접 수령으로 한 개의 상품을 주문한다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = FEMALE,
                    deliveryFee = Money.from(ZERO),
                    deliveryMethod = PICK_UP
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice,
                )

                requestSaveOrder(request, accessToken)

                Then("배송비와 배송 방법이 정상적으로 저장된다") {
                    val orders = orderRepository.findAll()

                    orders.map { it.orderProduct }.forAll {
                        it.deliveryMethod shouldBe PICK_UP
                        it.deliveryFee shouldBe Money.from(ZERO)
                    }
                }

                Then("배송번호는 한 개 생성된다") {
                    val orders = orderRepository.findAll()

                    orders.distinctBy { it.orderProduct.shippingNumber }.size shouldBe 1
                }
            }

            When("하나의 상점에서 직접 수령으로 여러 개의 상품을 주문한다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = FEMALE,
                    deliveryFee = Money.from(ZERO),
                    deliveryMethod = PICK_UP
                )
                val productOrderA2 = orderProductRequest(
                    product = productA2,
                    productOption = productOptionA2,
                    quantity = 1,
                    sex = MALE,
                    deliveryFee = Money.from(ZERO),
                    deliveryMethod = PICK_UP
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                    productOrderA2,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + productA2.discountPrice,
                )

                val response = requestSaveOrder(request, accessToken)

                Then("배송비와 배송 방법이 정상적으로 입력된다") {
                    val saveOrderResponse = response.`as`(SaveOrderResponse::class.java)
                    val order = orderRepository.findByOrderNumberOrThrow(OrderNumber(saveOrderResponse.orderId))[0]

                    order.orderProduct.deliveryMethod shouldBe PICK_UP
                    order.orderProduct.deliveryFee shouldBe Money.from(ZERO)
                }
            }

            When("주문한 상품이 존재하지 않으면") {
                val orderProductRequests = listOf(
                    orderProductRequest(
                        productId = MIN_VALUE,
                        storeId = storeA.id,
                    )
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = Money.from(ONE),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe PRODUCT_NOT_FOUND.errorMessage()
                    }
                }
            }

            When("주문한 상품의 옵션이 존재하지 않는다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = MALE,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe INVALID_PRODUCT_OPTION.errorMessage()
                    }
                }
            }

            When("주문 배송 정보가 존재하지 않는다면") {
                val productOrderA1 = orderProductRequest(
                    product = productA1,
                    productOption = productOptionA1,
                    quantity = 1,
                    sex = FEMALE,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = MIN_VALUE,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe NOT_FOUND_SHIPPING_ADDRESS.errorMessage()
                    }
                }
            }

            When("주문한 상품과 실제 상품의 본래 가격이 다르다면") {
                val productOrderA1 = orderProductRequest(
                    productId = productA1.id,
                    storeId = productA1.storeId,
                    quantity = 1,
                    originalPrice = productA1.price + 1,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice,
                    orderPrice = productA1.discountPrice,
                    sex = FEMALE.name,
                    additionalPrice = productOptionA1.additionalPrice,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON.name
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    // 실패
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_PRICE_NOT_MATCH.errorMessage()
                    }
                }
            }

            When("주문한 상품과 실제 상품의 할인 가격이 다르다면") {
                val productOrderA1 = orderProductRequest(
                    productId = productA1.id,
                    storeId = productA1.storeId,
                    quantity = 1,
                    originalPrice = productA1.price,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice + 1,
                    orderPrice = productA1.discountPrice + 1,
                    sex = FEMALE.name,
                    additionalPrice = productOptionA1.additionalPrice,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON.name
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + 1 + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_PRICE_NOT_MATCH.errorMessage()
                    }
                }
            }

            When("주문한 상품과 실제 상품의 옵션 추가 가격이 다르다면") {
                val productOrderA1 = orderProductRequest(
                    productId = productA1.id,
                    storeId = productA1.storeId,
                    quantity = 1,
                    originalPrice = productA1.price,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice,
                    orderPrice = productA1.discountPrice,
                    sex = FEMALE.name,
                    additionalPrice = productOptionA1.additionalPrice + 1,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON.name
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe INVALID_PRODUCT_OPTION.errorMessage()
                    }
                }
            }

            When("주문한 상품 가격의 합과 주문 가격이 다르다면") {
                val productOrderA1 = orderProductRequest(
                    productId = productA1.id,
                    storeId = productA1.storeId,
                    quantity = 1,
                    originalPrice = productA1.price,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice,
                    orderPrice = productA1.discountPrice,
                    sex = FEMALE.name,
                    additionalPrice = productOptionA1.additionalPrice,
                    deliveryFee = Money.from(3000),
                    deliveryMethod = COMMON.name
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + 1 + Money.from(3000),
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_PRICE_NOT_MATCH.errorMessage()
                    }
                }
            }

            When("주문한 상품의 배송비가 책정된 금액과 다르다면") {
                val productOrderA1 = orderProductRequest(
                    productId = productA1.id,
                    storeId = productA1.storeId,
                    quantity = 1,
                    originalPrice = productA1.price,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice,
                    orderPrice = productA1.discountPrice,
                    sex = FEMALE.name,
                    additionalPrice = productOptionA1.additionalPrice,
                    deliveryFee = Money.from(3000) + 1,
                    deliveryMethod = COMMON.name
                )

                val orderProductRequests = listOf(
                    productOrderA1,
                )

                val request = saveOrderRequest(
                    shippingAddressId = shippingAddress.id,
                    shippingRequest = "부재 시 경비실에 맡겨주세요.",
                    orderProductRequests = orderProductRequests,
                    totalAmount = productA1.discountPrice + Money.from(3000) + 1,
                )

                val response = requestSaveOrder(request, accessToken)

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_PRICE_NOT_MATCH.errorMessage()
                    }
                }
            }
        }
    }
}
