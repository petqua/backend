package com.petqua.application.wish

import com.petqua.application.product.DecreaseWishCountEvent
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.wish.WishProductRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.exception.wish.WishProductException
import com.petqua.exception.wish.WishProductExceptionType.ALREADY_EXIST_WISH_PRODUCT
import com.petqua.exception.wish.WishProductExceptionType.NOT_FOUND_WISH_PRODUCT
import com.petqua.presentation.wish.WishProductResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WishProductService(
    private val wishProductRepository: WishProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun save(command: SaveWishCommand) {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        productRepository.existByIdOrThrow(command.productId, ProductException(NOT_FOUND_PRODUCT))
        if (wishProductRepository.existsByProductIdAndMemberId(command.productId, command.memberId)) {
            throw WishProductException(ALREADY_EXIST_WISH_PRODUCT)
        }

        val wishProduct = command.toWishProduct()
        val product = productRepository.findByIdOrThrow(wishProduct.productId, ProductException(NOT_FOUND_PRODUCT))
        product.increaseWishCount()
        wishProductRepository.save(wishProduct)
    }

    fun delete(command: DeleteWishCommand) {
        val wishProduct =
            wishProductRepository.findByIdOrThrow(command.wishProductId, WishProductException(NOT_FOUND_WISH_PRODUCT))
        wishProduct.validateOwner(command.memberId)
        wishProductRepository.delete(wishProduct)
        publisher.publishEvent(DecreaseWishCountEvent(wishProduct.productId))
    }

    fun readAll(memberId: Long): List<WishProductResponse> {
        memberRepository.existByIdOrThrow(memberId, MemberException(NOT_FOUND_MEMBER))
        return wishProductRepository.readAllWishProductResponse(memberId)
    }
}
