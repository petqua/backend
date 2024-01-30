package com.petqua.application.cart

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
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
        val savedCartProduct = cartProductRepository.save(command.toCartProduct())
        return savedCartProduct.id
    }
}
