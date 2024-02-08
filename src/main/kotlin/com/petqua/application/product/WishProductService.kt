package com.petqua.application.product

import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.UpdateWishCommand
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProduct
import com.petqua.domain.product.WishProductRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.presentation.product.dto.WishProductsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WishProductService(
    private val wishProductRepository: WishProductRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
) {
    fun update(command: UpdateWishCommand) {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        wishProductRepository.findByProductIdAndMemberId(command.productId, command.memberId)
            ?.let { delete(it) } ?: save(command.toWishProduct())
    }

    private fun save(wishProduct: WishProduct) {
        wishProductRepository.save(wishProduct)
        val product =
            productRepository.findActiveByIdOrThrow(wishProduct.productId, ProductException(NOT_FOUND_PRODUCT))
        product.increaseWishCount()
    }

    private fun delete(wishProduct: WishProduct) {
        wishProductRepository.delete(wishProduct)
        val product = productRepository.findByIdOrThrow(wishProduct.productId, ProductException(NOT_FOUND_PRODUCT))
        product.decreaseWishCount()
    }

    @Transactional(readOnly = true)
    fun readAll(command: ReadAllWishProductCommand): WishProductsResponse {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        val totalWishProductsCount = wishProductRepository.countByMemberId(command.memberId)
        val wishProductResponse = wishProductRepository.readAllWishProductResponse(command.memberId, command.toPaging())
        return WishProductsResponse.of(wishProductResponse, command.limit, totalWishProductsCount)
    }
}
