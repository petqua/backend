package com.petqua.application.product

import com.petqua.application.product.dto.DeleteWishCommand
import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.SaveWishCommand
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProductRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.exception.product.WishProductException
import com.petqua.exception.product.WishProductExceptionType.ALREADY_EXIST_WISH_PRODUCT
import com.petqua.exception.product.WishProductExceptionType.NOT_FOUND_WISH_PRODUCT
import com.petqua.presentation.product.WishProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WishProductService(
    private val wishProductRepository: WishProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
) {
    fun save(command: SaveWishCommand) {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        if (wishProductRepository.existsByProductIdAndMemberId(command.productId, command.memberId)) {
            throw WishProductException(ALREADY_EXIST_WISH_PRODUCT)
        }
        val wishProduct = command.toWishProduct()
        wishProductRepository.save(wishProduct)
        val product = productRepository.findByIdOrThrow(wishProduct.productId, ProductException(NOT_FOUND_PRODUCT))
        product.increaseWishCount()
    }

    fun delete(command: DeleteWishCommand) {
        val wishProduct = wishProductRepository.findByIdOrThrow(
            id = command.wishProductId,
            e = WishProductException(NOT_FOUND_WISH_PRODUCT)
        )
        wishProduct.validateOwner(command.memberId)
        wishProductRepository.delete(wishProduct)
        val product = productRepository.findByIdOrThrow(wishProduct.productId, ProductException(NOT_FOUND_PRODUCT))
        product.decreaseWishCount()
    }

    @Transactional(readOnly = true)
    fun readAll(command: ReadAllWishProductCommand): List<WishProductResponse> {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        return wishProductRepository.readAllWishProductResponse(command.memberId, command.toPaging())
    }
}
