package com.petqua.application.order

import com.petqua.application.order.dto.ReadDefaultShippingAddressResponse
import com.petqua.application.order.dto.SaveShippingAddressCommand
import com.petqua.application.order.dto.SaveShippingAddressResponse
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.ShippingAddress
import com.petqua.domain.order.ShippingAddressRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.order.ShippingAddressException
import com.petqua.exception.order.ShippingAddressExceptionType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ShippingAddressService(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val memberRepository: MemberRepository,
) {

    fun save(command: SaveShippingAddressCommand): SaveShippingAddressResponse {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        if (command.isDefaultAddress) {
            unsetPrevDefaultShippingAddress(command.memberId)
        }

        val id = shippingAddressRepository.save(
            ShippingAddress(
                memberId = command.memberId,
                name = command.name,
                receiver = command.receiver,
                phoneNumber = command.phoneNumber,
                zipCode = command.zipCode,
                address = command.address,
                detailAddress = command.detailAddress,
                isDefaultAddress = command.isDefaultAddress
            )
        ).id
        return SaveShippingAddressResponse(id = id)
    }

    private fun unsetPrevDefaultShippingAddress(memberId: Long) {
        val prevDefaultAddress = shippingAddressRepository.findByMemberIdAndIsDefaultAddress(memberId)
        prevDefaultAddress?.let {
            it.isDefaultAddress = false
        }
    }

    @Transactional(readOnly = true)
    fun readDefaultShippingAddress(memberId: Long): ReadDefaultShippingAddressResponse {
        val shippingAddress = shippingAddressRepository.findByMemberIdAndIsDefaultAddress(memberId)
            ?: throw ShippingAddressException(ShippingAddressExceptionType.NOT_FOUND_SHIPPING_ADDRESS)
        return ReadDefaultShippingAddressResponse.from(shippingAddress)
    }
}
