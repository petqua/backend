package com.petqua.domain.order

import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType
import com.petqua.test.fixture.shippingAddress
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ShippingAddressTest : StringSpec({

    "배송지 이름이 비어 있을 경우 생성 실패" {
        shouldThrow<ShippingAddressException> {
            shippingAddress(name = "")
        }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_NAME
    }

    "받는 사람이 비어 있을 경우 생성 실패" {
        shouldThrow<ShippingAddressException> {
            shippingAddress(receiver = "")
        }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_RECEIVER
    }

    "주소가 비어 있을 경우 생성 실패" {
        shouldThrow<ShippingAddressException> {
            shippingAddress(address = "")
        }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_ADDRESS
    }

    "상세주소가 비어 있을 경우 생성 실패" {
        shouldThrow<ShippingAddressException> {
            shippingAddress(detailAddress = "")
        }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_DETAIL_ADDRESS
    }

    "전화번호 형식이 잘못되었을 경우 생성 실패" {
        listOf(
            "012-1234-1234",
            "010-123-1234",
            "010-1234-123",
            "010-12345-1234",
            "010-1234-12345"
        ).forAll { phoneNumber ->
            shouldThrow<ShippingAddressException> {
                shippingAddress(phoneNumber = phoneNumber)
            }.exceptionType() shouldBe ShippingAddressExceptionType.INVALID_PHONE_NUMBER
        }
    }
})

