package com.petqua.domain.order

import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.domain.member.MemberRepository
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class OrderCustomRepositoryImplTest(
    private val orderRepository: OrderRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    /**
     * 테스트 데이터 Order
     * OrderA - A1, A2, A3
     * OrderB - B1, B2
     * OrderC - C1
     */
    Given("주문을 조회 할 때") {
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

        When("최초 주문 조회시 주문ID와 주문번호는 입력 하지 않아도") {
            val orderPagingRequest = OrderPaging.of(
                lastViewedId = DEFAULT_LAST_VIEWED_ID, // 최초 조회 플래그
                limit = 1,
                lastViewedOrderNumber = null
            )

            val result = orderRepository.findOrdersByMemberId(
                memberId = member.id,
                orderPaging = orderPagingRequest,
            )

            Then("주문 내역이 조회된다.") {
                result.forEach { println(it.orderProduct.productName) }
                result.size shouldBe 3
                result.map { it.orderProduct.productName } shouldBe listOf("C1", "B2", "B1")
            }
        }

        When("마지막으로 조회된 주문의 ID와 주문 번호를 기준으로") {
            val orderPagingRequest = OrderPaging.of(
                lastViewedId = orderC1.id,
                limit = 1,
                lastViewedOrderNumber = orderNumberC
            )

            val result = orderRepository.findOrdersByMemberId(
                memberId = member.id,
                orderPaging = orderPagingRequest,
            )

            Then("주문 내역이 조회된다.") {
                result.size shouldBe 5
                result.map { it.orderProduct.productName } shouldBe listOf("B2", "B1", "A3", "A2", "A1")

            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
