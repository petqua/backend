package com.petqua.application.cart

import com.petqua.application.cart.dto.CartProductWithSupportedOptionResponse
import com.petqua.application.cart.dto.DeleteCartProductCommand
import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.option.ProductOptionRepository
import com.petqua.domain.product.option.Sex
import com.petqua.domain.product.option.Sex.FEMALE
import com.petqua.domain.product.option.Sex.MALE
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.DIFFERENT_DELIVERY_FEE
import com.petqua.exception.cart.CartProductExceptionType.DUPLICATED_PRODUCT
import com.petqua.exception.cart.CartProductExceptionType.NOT_EXIST_OPTION
import com.petqua.exception.cart.CartProductExceptionType.NOT_FOUND_CART_PRODUCT
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
@Service
class CartProductService(
    private val cartProductRepository: CartProductRepository,
    private val productRepository: ProductRepository,
    private val productOptionRepository: ProductOptionRepository,
    private val memberRepository: MemberRepository,
) {

    fun save(command: SaveCartProductCommand): Long {
        memberRepository.existByIdOrThrow(command.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        val product = productRepository.findByIdOrThrow(command.productId) {
            ProductException(NOT_FOUND_PRODUCT)
        }
        validateDuplicatedProduct(
            memberId = command.memberId,
            productId = command.productId,
            sex = command.sex,
            deliveryMethod = command.deliveryMethod
        )
        validateSupportedOption(command.productId, command.sex)
        validateDeliveryFee(product, command.deliveryMethod, command.deliveryFee)
        return cartProductRepository.save(command.toCartProduct()).id
    }

    private fun validateSupportedOption(productId: Long, sex: Sex) {
        throwExceptionWhen(!productOptionRepository.existsByProductIdAndSex(productId, sex)) {
            CartProductException(NOT_EXIST_OPTION)
        }
    }

    fun updateOptions(command: UpdateCartProductOptionCommand) {
        val cartProduct = cartProductRepository.findByIdOrThrow(command.cartProductId) {
            CartProductException(NOT_FOUND_CART_PRODUCT)
        }
        cartProduct.validateOwner(command.memberId)
        validateDuplicatedProduct(
            memberId = command.memberId,
            productId = cartProduct.productId,
            sex = command.sex,
            deliveryMethod = command.deliveryMethod
        )
        val product = productRepository.findByIdOrThrow(cartProduct.productId) {
            ProductException(NOT_FOUND_PRODUCT)
        }
        validateSupportedOption(product.id, command.sex)
        validateDeliveryFee(product, command.deliveryMethod, command.deliveryFee)
        cartProduct.updateOptions(
            command.quantity,
            command.sex,
            command.deliveryMethod,
            command.deliveryFee
        )
    }

    private fun validateDeliveryFee(product: Product, deliveryMethod: DeliveryMethod, deliveryFee: BigDecimal) {
        product.getDeliveryFee(deliveryMethod)
            .also { throwExceptionWhen(it != deliveryFee) { CartProductException(DIFFERENT_DELIVERY_FEE) } }
    }

    private fun validateDuplicatedProduct(
        memberId: Long,
        productId: Long,
        sex: Sex,
        deliveryMethod: DeliveryMethod
    ) {
        cartProductRepository.findByMemberIdAndProductIdAndSexAndDeliveryMethod(
            memberId = memberId,
            productId = productId,
            sex = sex,
            deliveryMethod = deliveryMethod
        )?.also { throw CartProductException(DUPLICATED_PRODUCT) }
    }

    fun delete(command: DeleteCartProductCommand) {
        val cartProduct = cartProductRepository.findByIdOrThrow(command.cartProductId) {
            CartProductException(NOT_FOUND_CART_PRODUCT)
        }
        cartProduct.validateOwner(command.memberId)
        cartProductRepository.delete(cartProduct)
    }

    @Transactional(readOnly = true)
    fun readAll(memberId: Long): List<CartProductWithSupportedOptionResponse> {
        memberRepository.existByIdOrThrow(memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        val cartByProduct = cartProductRepository.findAllCartResultsByMemberId(memberId).associateBy { it.productId }
        val productOptions = productOptionRepository.findByProductIdIn(cartByProduct.map { it.key })
        val maleProductOption = productOptions.filter { it.sex == MALE }.associateBy { it.productId }
        val femaleProductOption = productOptions.filter { it.sex == FEMALE }.associateBy { it.productId }

        return cartByProduct.values.map {
            CartProductWithSupportedOptionResponse(
                it,
                maleProductOption[it.productId]?.additionalPrice,
                femaleProductOption[it.productId]?.additionalPrice
            )
        }
    }
}
