package com.petqua.application.cart

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.cart.DeliveryMethod
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.DUPLICATED_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.NOT_FOUND_CART_PRODUCT
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CartProductService(
    private val cartProductRepository: CartProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
) {

    fun save(command: SaveCartProductCommand): Long {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        productRepository.existByIdOrThrow(command.productId, ProductException(NOT_FOUND_PRODUCT))
        validateDuplicatedProduct(
            memberId = command.memberId,
            productId = command.productId,
            isMale = command.isMale,
            deliveryMethod = command.deliveryMethod
        )
        return cartProductRepository.save(command.toCartProduct()).id
    }

    fun updateOptions(command: UpdateCartProductOptionCommand) {
        val cartProduct = cartProductRepository.findByIdOrThrow(
            command.cartProductId,
            CartProductException(NOT_FOUND_CART_PRODUCT)
        )
        cartProduct.validateOwner(command.memberId)
        validateDuplicatedProduct(
            memberId = command.memberId,
            productId = cartProduct.productId,
            isMale = command.isMale,
            deliveryMethod = command.deliveryMethod
        )
        cartProduct.updateOptions(command.quantity, command.isMale, command.deliveryMethod)
    }

    private fun validateDuplicatedProduct(
        memberId: Long,
        productId: Long,
        isMale: Boolean,
        deliveryMethod: DeliveryMethod
    ) {
        cartProductRepository.findByMemberIdAndProductIdAndIsMaleAndDeliveryMethod(
            memberId = memberId,
            productId = productId,
            isMale = isMale,
            deliveryMethod = deliveryMethod
        )?.also { throw CartProductException(DUPLICATED_PRODUCT) }
    }
}
