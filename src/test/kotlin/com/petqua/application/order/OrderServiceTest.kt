package com.petqua.application.order

import com.petqua.application.order.dto.OrderDetailReadQuery
import com.petqua.application.order.dto.OrderReadQuery
import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.domain.delivery.DeliveryMethod.COMMON
import com.petqua.domain.delivery.DeliveryMethod.SAFETY
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.ProductSnapshot
import com.petqua.domain.product.ProductSnapshotRepository
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.NOT_INVALID_ORDER_READ_QUERY
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.ORDER_TOTAL_PRICE_NOT_MATCH
import com.petqua.exception.order.OrderExceptionType.PRODUCT_INFO_NOT_MATCH
import com.petqua.exception.order.OrderExceptionType.PRODUCT_NOT_FOUND
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_PRODUCT_OPTION
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import com.petqua.test.fixture.orderPayment
import com.petqua.test.fixture.orderProductCommand
import com.petqua.test.fixture.product
import com.petqua.test.fixture.productOption
import com.petqua.test.fixture.saveOrderCommand
import com.petqua.test.fixture.shippingAddress
import com.petqua.test.fixture.store
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class OrderServiceTest(
    private val orderService: OrderService,
    private val orderRepository: OrderRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val memberRepository: MemberRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val productSnapshotRepository: ProductSnapshotRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("주문을 저장 할 때") {

        When("상품이 존재 하지 않으면") {
            val memberId = memberRepository.save(member()).id
            val productA = productRepository.save(product())
            val productB = productRepository.save(product())
            productSnapshotRepository.save(ProductSnapshot.from(productA))
            productSnapshotRepository.save(ProductSnapshot.from(productB))
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA.id,
                ),
                orderProductCommand(
                    productId = productB.id,
                ),
                orderProductCommand(
                    productId = Long.MIN_VALUE,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (PRODUCT_NOT_FOUND)
            }
        }

        When("유효하지 않은 상품 옵션을 입력하면") {
            val memberId = memberRepository.save(member()).id
            val productA = productRepository.save(product())
            val productB = productRepository.save(product())
            productSnapshotRepository.save(ProductSnapshot.from(productA))
            productSnapshotRepository.save(ProductSnapshot.from(productB))
            val productOptionA = productOptionRepository.save(
                productOption(
                    productId = productA.id,
                    sex = FEMALE,
                    additionalPrice = BigDecimal.ONE
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE,
                    additionalPrice = BigDecimal.TEN
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA.id,
                    sex = Sex.HERMAPHRODITE,
                ),
                orderProductCommand(
                    productId = productB.id,
                    sex = productOptionB.sex,
                    additionalPrice = productOptionB.additionalPrice.value,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
            )

            Then("예외가 발생 한다") {
                shouldThrow<ProductException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (INVALID_PRODUCT_OPTION)
            }
        }

        When("존재 하지 않는 배송 정보를 입력 하면") {
            val memberId = memberRepository.save(member()).id
            val productA = productRepository.save(product(commonDeliveryFee = 3000.toBigDecimal()))
            productSnapshotRepository.save(ProductSnapshot.from(productA))
            val productOptionA = productOptionRepository.save(
                productOption(
                    productId = productA.id,
                    sex = FEMALE,
                    additionalPrice = 1.toBigDecimal()
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA.id,
                    sex = productOptionA.sex,
                    additionalPrice = productOptionA.additionalPrice.value,
                    orderPrice = 2.toBigDecimal()
                ),
            )

            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = Long.MIN_VALUE,
                totalAmount = 3002.toBigDecimal()
            )

            Then("예외가 발생 한다") {
                shouldThrow<ShippingAddressException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (NOT_FOUND_SHIPPING_ADDRESS)
            }
        }

        When("주문 상품과 실제 상품의 가격이 다르면") {
            val memberId = memberRepository.save(member()).id
            val productA = productRepository.save(
                product(
                    price = BigDecimal.TEN,
                    discountRate = 10,
                    discountPrice = 9.toBigDecimal(),
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productB = productRepository.save(
                product(
                    price = BigDecimal.TEN,
                    discountRate = 10,
                    discountPrice = 9.toBigDecimal(),
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            productSnapshotRepository.save(ProductSnapshot.from(productA))
            productSnapshotRepository.save(ProductSnapshot.from(productB))

            val productOptionA = productOptionRepository.save(
                productOption(
                    productId = productA.id,
                    sex = FEMALE,
                    additionalPrice = 1.toBigDecimal()
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE,
                    additionalPrice = 10.toBigDecimal()
                )
            )
            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA.id,
                    originalPrice = productA.price.value,
                    discountRate = productA.discountRate,
                    discountPrice = productA.discountPrice.value,
                    sex = productOptionA.sex,
                    orderPrice = BigDecimal.TEN,
                    additionalPrice = productOptionA.additionalPrice.value,
                    deliveryFee = productA.commonDeliveryFee!!.value,
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productB.id,
                    originalPrice = productB.price.value,
                    discountRate = productB.discountRate,
                    discountPrice = productB.discountPrice.value,
                    sex = productOptionB.sex,
                    orderPrice = 9.toBigDecimal(),
                    additionalPrice = productOptionB.additionalPrice.value,
                    deliveryFee = productB.safeDeliveryFee!!.value,
                    deliveryMethod = SAFETY,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = shippingAddress.id,
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (ORDER_TOTAL_PRICE_NOT_MATCH)
            }
        }

        When("주문 상품과 실제 상품의 배송비가 다르면") {
            val memberId = memberRepository.save(member()).id
            val productA = productRepository.save(
                product(
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productB = productRepository.save(
                product(
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            productSnapshotRepository.save(ProductSnapshot.from(productA))
            productSnapshotRepository.save(ProductSnapshot.from(productB))

            val productOptionA = productOptionRepository.save(
                productOption(
                    productId = productA.id,
                    sex = FEMALE,
                    additionalPrice = 1.toBigDecimal()
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE,
                    additionalPrice = 10.toBigDecimal()
                )
            )
            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA.id,
                    originalPrice = productA.price.value,
                    discountRate = productA.discountRate,
                    discountPrice = productA.discountPrice.value,
                    sex = productOptionA.sex,
                    orderPrice = BigDecimal.TEN,
                    additionalPrice = productOptionA.additionalPrice.value,
                    deliveryFee = 1.toBigDecimal(),
                ),
                orderProductCommand(
                    productId = productB.id,
                    originalPrice = productB.price.value,
                    discountRate = productB.discountRate,
                    discountPrice = productB.discountPrice.value,
                    sex = productOptionB.sex,
                    orderPrice = 9.toBigDecimal(),
                    additionalPrice = productOptionB.additionalPrice.value,
                    deliveryFee = 5000.toBigDecimal(),
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = shippingAddress.id,
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (PRODUCT_INFO_NOT_MATCH)
            }
        }

        When("전체 주문 금액이 일치 하지 않으면") {
            val memberId = memberRepository.save(member()).id
            val productA1 = productRepository.save(
                product(
                    storeId = 1L,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productA2 = productRepository.save(
                product(
                    storeId = 1L,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productB = productRepository.save(
                product(
                    storeId = 2L,
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            productSnapshotRepository.save(ProductSnapshot.from(productA1))
            productSnapshotRepository.save(ProductSnapshot.from(productA2))
            productSnapshotRepository.save(ProductSnapshot.from(productB))

            val productOptionA1 = productOptionRepository.save(
                productOption(
                    productId = productA1.id,
                    sex = FEMALE,
                )
            )
            val productOptionA2 = productOptionRepository.save(
                productOption(
                    productId = productA2.id,
                    sex = FEMALE,
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE
                )
            )
            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA1.id,
                    originalPrice = productA1.price.value,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice.value,
                    sex = productOptionA1.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA1.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productA2.id,
                    originalPrice = productA2.price.value,
                    discountRate = productA2.discountRate,
                    discountPrice = productA2.discountPrice.value,
                    sex = productOptionA2.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA2.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productB.id,
                    originalPrice = productB.price.value,
                    discountRate = productB.discountRate,
                    discountPrice = productB.discountPrice.value,
                    sex = productOptionB.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionB.additionalPrice.value,
                    deliveryFee = 5000.toBigDecimal(),
                    deliveryMethod = SAFETY,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = shippingAddress.id,
                totalAmount = 11003.toBigDecimal(),
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (ORDER_TOTAL_PRICE_NOT_MATCH)
            }
        }

        When("상품의 스냅샷이 존재하지 않으면") {
            val memberId = memberRepository.save(member()).id
            val productA1 = productRepository.save(
                product(
                    storeId = 1L,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productA2 = productRepository.save(
                product(
                    storeId = 1L,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productB = productRepository.save(
                product(
                    storeId = 2L,
                    safeDeliveryFee = 5000.toBigDecimal(),
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
                    sex = FEMALE,
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE
                )
            )
            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA1.id,
                    originalPrice = productA1.price.value,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice.value,
                    sex = productOptionA1.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA1.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productA2.id,
                    originalPrice = productA2.price.value,
                    discountRate = productA2.discountRate,
                    discountPrice = productA2.discountPrice.value,
                    sex = productOptionA2.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA2.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productB.id,
                    originalPrice = productB.price.value,
                    discountRate = productB.discountRate,
                    discountPrice = productB.discountPrice.value,
                    sex = productOptionB.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionB.additionalPrice.value,
                    deliveryFee = 5000.toBigDecimal(),
                    deliveryMethod = SAFETY,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = shippingAddress.id,
                totalAmount = 8003.toBigDecimal(),
            )

            Then("예외가 발생 한다") {
                shouldThrow<ProductException> {
                    orderService.save(command)
                }.exceptionType() shouldBe (NOT_FOUND_PRODUCT)
            }
        }
    }

    Given("주문 생성을") {

        When("명령하면") {
            val storeAId = storeRepository.save(store()).id
            val storeBId = storeRepository.save(store()).id
            val memberId = memberRepository.save(member()).id
            val productA1 = productRepository.save(
                product(
                    storeId = storeAId,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productA2 = productRepository.save(
                product(
                    storeId = storeAId,
                    commonDeliveryFee = 3000.toBigDecimal(),
                )
            )
            val productB = productRepository.save(
                product(
                    storeId = storeBId,
                    safeDeliveryFee = 5000.toBigDecimal(),
                )
            )
            productSnapshotRepository.save(ProductSnapshot.from(productA1))
            productSnapshotRepository.save(ProductSnapshot.from(productA2))
            productSnapshotRepository.save(ProductSnapshot.from(productB))

            val productOptionA1 = productOptionRepository.save(
                productOption(
                    productId = productA1.id,
                    sex = FEMALE,
                )
            )
            val productOptionA2 = productOptionRepository.save(
                productOption(
                    productId = productA2.id,
                    sex = FEMALE,
                )
            )
            val productOptionB = productOptionRepository.save(
                productOption(
                    productId = productB.id,
                    sex = Sex.HERMAPHRODITE
                )
            )
            val shippingAddress = shippingAddressRepository.save(
                shippingAddress(
                    memberId = memberId,
                )
            )
            val orderProductCommands = listOf(
                orderProductCommand(
                    productId = productA1.id,
                    originalPrice = productA1.price.value,
                    discountRate = productA1.discountRate,
                    discountPrice = productA1.discountPrice.value,
                    sex = productOptionA1.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA1.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productA2.id,
                    originalPrice = productA2.price.value,
                    discountRate = productA2.discountRate,
                    discountPrice = productA2.discountPrice.value,
                    sex = productOptionA2.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionA2.additionalPrice.value,
                    deliveryFee = 3000.toBigDecimal(),
                    deliveryMethod = COMMON,
                ),
                orderProductCommand(
                    productId = productB.id,
                    originalPrice = productB.price.value,
                    discountRate = productB.discountRate,
                    discountPrice = productB.discountPrice.value,
                    sex = productOptionB.sex,
                    orderPrice = BigDecimal.ONE,
                    additionalPrice = productOptionB.additionalPrice.value,
                    deliveryFee = 5000.toBigDecimal(),
                    deliveryMethod = SAFETY,
                ),
            )
            val command = saveOrderCommand(
                memberId = memberId,
                orderProductCommands = orderProductCommands,
                shippingAddressId = shippingAddress.id,
                totalAmount = 8003.toBigDecimal(),
            )
            val response = orderService.save(command)

            Then("주문이 저장된다") {
                val orders = orderRepository.findAll()
                orders.forAll {
                    it.orderNumber.value shouldBe response.orderNumber
                    it.orderName.value shouldBe response.orderName
                }
                orders.distinctBy { it.orderProduct.shippingNumber }.size shouldBe 2

                val orderIds = orders.map { it.id }
                val orderPayments = orderPaymentRepository.findLatestAllByOrderIds(orderIds)
                orderPayments.forAll {
                    it.status shouldBe ORDER_CREATED
                }
            }
        }
    }

    Given("주문 상세 내역 조회 시") {
        val storeAId = storeRepository.save(store()).id
        val storeBId = storeRepository.save(store()).id
        val memberId = memberRepository.save(member()).id
        val productA1 = productRepository.save(
            product(
                storeId = storeAId,
                commonDeliveryFee = 3000.toBigDecimal(),
            )
        )
        val productA2 = productRepository.save(
            product(
                storeId = storeAId,
                commonDeliveryFee = 3000.toBigDecimal(),
            )
        )
        val productB = productRepository.save(
            product(
                storeId = storeBId,
                safeDeliveryFee = 5000.toBigDecimal(),
            )
        )
        productSnapshotRepository.save(ProductSnapshot.from(productA1))
        productSnapshotRepository.save(ProductSnapshot.from(productA2))
        productSnapshotRepository.save(ProductSnapshot.from(productB))

        val productOptionA1 = productOptionRepository.save(
            productOption(
                productId = productA1.id,
                sex = FEMALE,
            )
        )
        val productOptionA2 = productOptionRepository.save(
            productOption(
                productId = productA2.id,
                sex = FEMALE,
            )
        )
        val productOptionB = productOptionRepository.save(
            productOption(
                productId = productB.id,
                sex = Sex.HERMAPHRODITE
            )
        )
        val shippingAddress = shippingAddressRepository.save(
            shippingAddress(
                memberId = memberId,
            )
        )
        val orderProductCommands = listOf(
            orderProductCommand(
                productId = productA1.id,
                originalPrice = productA1.price.value,
                discountRate = productA1.discountRate,
                discountPrice = productA1.discountPrice.value,
                sex = productOptionA1.sex,
                orderPrice = BigDecimal.ONE,
                additionalPrice = productOptionA1.additionalPrice.value,
                deliveryFee = 3000.toBigDecimal(),
                deliveryMethod = COMMON,
            ),
            orderProductCommand(
                productId = productA2.id,
                originalPrice = productA2.price.value,
                discountRate = productA2.discountRate,
                discountPrice = productA2.discountPrice.value,
                sex = productOptionA2.sex,
                orderPrice = BigDecimal.ONE,
                additionalPrice = productOptionA2.additionalPrice.value,
                deliveryFee = 3000.toBigDecimal(),
                deliveryMethod = COMMON,
            ),
            orderProductCommand(
                productId = productB.id,
                originalPrice = productB.price.value,
                discountRate = productB.discountRate,
                discountPrice = productB.discountPrice.value,
                sex = productOptionB.sex,
                orderPrice = BigDecimal.ONE,
                additionalPrice = productOptionB.additionalPrice.value,
                deliveryFee = 5000.toBigDecimal(),
                deliveryMethod = SAFETY,
            ),
        )
        val command = saveOrderCommand(
            memberId = memberId,
            orderProductCommands = orderProductCommands,
            shippingAddressId = shippingAddress.id,
            totalAmount = 8003.toBigDecimal(),
        )
        val saveOrderResponse = orderService.save(command)

        When("주문시 생성된 주문 번호로 조회하면") {
            val query = OrderDetailReadQuery(
                memberId = memberId,
                orderNumber = OrderNumber.from(saveOrderResponse.orderNumber),
            )

            val readDetail = orderService.readDetail(query)

            Then("주문 상세 내역이 조회된다") {
                assertSoftly(readDetail) {
                    orderProducts.size shouldBe 3
                    orderProducts.forAll {
                        it.orderStatus shouldBe ORDER_CREATED.name
                    }
                }
            }
        }

        When("본인이 주문한 주문이 아니면") {
            val query = OrderDetailReadQuery(
                memberId = Long.MIN_VALUE,
                orderNumber = OrderNumber.from(saveOrderResponse.orderNumber),
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.readDetail(query)
                }.exceptionType() shouldBe (FORBIDDEN_ORDER)
            }
        }

        When("존재 하지 않는 주문번호로 조회하면") {
            val query = OrderDetailReadQuery(
                memberId = memberId,
                orderNumber = OrderNumber.from("199902211607026029E90DB030"),
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.readDetail(query)
                }.exceptionType() shouldBe (ORDER_NOT_FOUND)
            }
        }
    }

    Given("주문 내역 조회 시") {
        val member = memberRepository.save(member())

        val orderNumberA = OrderNumber.from("202202211607020ORDERNUMBER")
        val orderA1 = order(memberId = member.id, orderNumber = orderNumberA, productName = "A1")
        val orderA2 = order(memberId = member.id, orderNumber = orderNumberA, productName = "A2")
        val orderA3 = order(memberId = member.id, orderNumber = orderNumberA, productName = "A3")

        val orderNumberB = OrderNumber.from("202302211607020ORDERNUMBER")
        val orderB1 = order(memberId = member.id, orderNumber = orderNumberB, productName = "B1")
        val orderB2 = order(memberId = member.id, orderNumber = orderNumberB, productName = "B2")


        val orderNumberC = OrderNumber.from("202402211607020ORDERNUMBER")
        val orderC1 = order(memberId = member.id, orderNumber = orderNumberC, productName = "C1")

        orderRepository.saveAll(
            listOf(
                orderA1, orderA2, orderA3,
                orderB1, orderB2,
                orderC1
            )
        )

        orderPaymentRepository.saveAll(
            listOf(
                orderPayment(orderId = orderA1.id, prevId = orderA1.id),
                orderPayment(orderId = orderA2.id, prevId = orderA2.id),
                orderPayment(orderId = orderA3.id, prevId = orderA3.id),
                orderPayment(orderId = orderB1.id, prevId = orderB1.id),
                orderPayment(orderId = orderB2.id, prevId = orderB2.id),
                orderPayment(orderId = orderC1.id, prevId = orderC1.id),
            )
        )

        When("최초 주문 조회시 주문ID와 주문번호는 입력 하지 않아도") {
            val query = OrderReadQuery(
                memberId = member.id,
                lastViewedId = DEFAULT_LAST_VIEWED_ID,
                limit = 2,
                lastViewedOrderNumber = null,
            )
            val result = orderService.readAll(query)

            Then("주문 내역이 조회된다.") {
                assertSoftly(result) {
                    orders.size shouldBe 2
                    orders[0].orderProducts.map { it.productName } shouldBe listOf("C1")
                    orders[1].orderProducts.map { it.productName } shouldBe listOf("B2", "B1")
                    hasNextPage shouldBe true
                }
            }
        }

        When("마지막으로 조회된 주문의 ID와 주문 번호를 기준으로") {
            val query = OrderReadQuery(
                memberId = member.id,
                lastViewedId = orderC1.id,
                limit = 2,
                lastViewedOrderNumber = orderNumberC,
            )
            val result = orderService.readAll(query)

            Then("주문 내역이 조회된다.") {
                assertSoftly(result) {
                    orders.size shouldBe 2
                    orders[0].orderProducts.map { it.productName } shouldBe listOf("B2", "B1")
                    orders[1].orderProducts.map { it.productName } shouldBe listOf("A3", "A2", "A1")
                    hasNextPage shouldBe false
                }
            }
        }

        When("조회 조건에 주문번호와 ID가 다르면") {
            val query = OrderReadQuery(
                memberId = member.id,
                lastViewedId = orderC1.id,
                limit = 2,
                lastViewedOrderNumber = orderNumberA,
            )

            Then("예외가 발생 한다") {
                shouldThrow<OrderException> {
                    orderService.readAll(query)
                }.exceptionType() shouldBe (NOT_INVALID_ORDER_READ_QUERY)
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
