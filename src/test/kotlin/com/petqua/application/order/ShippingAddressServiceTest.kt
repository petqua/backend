package com.petqua.application.order

import com.petqua.application.order.dto.SaveShippingAddressCommand
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.shippingAddress
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = NONE)
class ShippingAddressServiceTest(
    private val shippingAddressService: ShippingAddressService,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("배송지 저장 명령으로") {
        val memberId = memberRepository.save(member()).id
        val command = SaveShippingAddressCommand(
            memberId = memberId,
            name = "집",
            receiver = "홍길동",
            phoneNumber = "010-1234-5678",
            zipCode = 12345,
            address = "서울시 강남구 역삼동 99번길",
            detailAddress = "101동 101호",
            isDefaultAddress = true
        )

        When("배송지를") {
            val response = shippingAddressService.save(command)

            Then("저장한다") {
                shippingAddressRepository.findById(response.id).shouldNotBeNull()
            }
        }
    }

    Given("기본 배송지가 있는 상태에서") {
        val memberId = memberRepository.save(member()).id
        val prevDefaultShippingAddress = shippingAddressRepository.save(
            shippingAddress(
                memberId = memberId,
                isDefaultAddress = true,
            )
        )
        val command = SaveShippingAddressCommand(
            memberId = memberId,
            name = "집",
            receiver = "홍길동",
            phoneNumber = "010-1234-5678",
            zipCode = 12345,
            address = "서울시 강남구 역삼동 99번길",
            detailAddress = "101동 101호",
            isDefaultAddress = true
        )

        When("새로운 기본 배송지를 생성하면") {
            val response = shippingAddressService.save(command)

            Then("이전 기본 배송지는 삭제되고, 새로운 배송지가 기본 배송지가 된다") {
                shippingAddressRepository.existsById(prevDefaultShippingAddress.id).shouldBeFalse()
                shippingAddressRepository.findByIdOrThrow(response.id).isDefaultAddress.shouldBeTrue()
            }
        }
    }

    Given("배송지 저장 요청시") {
        val memberId = memberRepository.save(member()).id

        When("멤버가 존재하지 않으면") {
            val command = SaveShippingAddressCommand(
                memberId = MIN_VALUE,
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<MemberException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe MemberExceptionType.NOT_FOUND_MEMBER
            }
        }

        When("전화번호 형식이 올바르지 않으면") {
            val command = SaveShippingAddressCommand(
                memberId = memberId,
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-123-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<ShippingAddressException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe ShippingAddressExceptionType.INVALID_PHONE_NUMBER
            }
        }

        When("배송지 이름이 비어있으면") {
            val command = SaveShippingAddressCommand(
                memberId = memberId,
                name = "",
                receiver = "홍길동",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<ShippingAddressException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_NAME
            }
        }

        When("받는 사람이 비어있으면") {
            val command = SaveShippingAddressCommand(
                memberId = memberId,
                name = "집",
                receiver = "",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "101동 101호",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<ShippingAddressException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_RECEIVER
            }
        }

        When("주소가 비어있으면") {
            val command = SaveShippingAddressCommand(
                memberId = memberId,
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "",
                detailAddress = "101동 101호",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<ShippingAddressException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_ADDRESS
            }
        }

        When("상세주소가 비어있으면") {
            val command = SaveShippingAddressCommand(
                memberId = memberId,
                name = "집",
                receiver = "홍길동",
                phoneNumber = "010-1234-5678",
                zipCode = 12345,
                address = "서울시 강남구 역삼동 99번길",
                detailAddress = "",
                isDefaultAddress = true
            )

            Then("예외가 발생한다") {
                shouldThrow<ShippingAddressException> {
                    shippingAddressService.save(command)
                }.exceptionType() shouldBe ShippingAddressExceptionType.EMPTY_DETAIL_ADDRESS
            }
        }
    }

    Given("기본 배송지 조회 명령으로") {
        val memberId = memberRepository.save(member()).id
        val savedShippingAddress = shippingAddressRepository.save(
            shippingAddress(
                memberId = memberId,
                isDefaultAddress = true
            )
        )

        When("멤버의 기본 배송지를") {
            val response = shippingAddressService.readDefaultShippingAddress(memberId)

            Then("조회한다") {
                assertSoftly(response) {
                    response.shouldNotBeNull()
                    response.id shouldBe savedShippingAddress.id
                    response.name shouldBe savedShippingAddress.name
                    response.receiver shouldBe savedShippingAddress.receiver
                    response.phoneNumber shouldBe savedShippingAddress.phoneNumber
                    response.zipCode shouldBe savedShippingAddress.zipCode
                    response.address shouldBe savedShippingAddress.address
                    response.detailAddress shouldBe savedShippingAddress.detailAddress
                }
            }
        }
    }

    Given("기본 배송지 조회 명령시") {
        val memberId = memberRepository.save(member()).id
        shippingAddressRepository.save(
            shippingAddress(
                memberId = memberId,
                isDefaultAddress = false
            )
        )

        When("멤버의 기본 배송지가 존재하지 않으면") {
            val response = shippingAddressService.readDefaultShippingAddress(memberId)

            Then("빈 응답이 반환된다") {
                response.shouldBeNull()
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
