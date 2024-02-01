package com.petqua.application.wish

import com.petqua.application.product.DecreaseWishCountEvent
import com.petqua.common.domain.existByIdOrThrow
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.wish.WishRepository
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.exception.wish.WishException
import com.petqua.exception.wish.WishExceptionType.ALREADY_EXIST_WISH
import com.petqua.exception.wish.WishExceptionType.NOT_FOUND_WISH
import com.petqua.presentation.wish.WishResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class WishService(
    private val wishRepository: WishRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val publisher: ApplicationEventPublisher,
) {
    fun save(command: SaveWishCommand) {
        memberRepository.existByIdOrThrow(command.memberId, MemberException(NOT_FOUND_MEMBER))
        productRepository.existByIdOrThrow(command.productId, ProductException(NOT_FOUND_PRODUCT))
        if (wishRepository.existsByProductIdAndMemberId(command.productId, command.memberId)) {
            return
        }

        val wish = command.toWish()
        val product = productRepository.findByIdOrThrow(wish.productId, ProductException(NOT_FOUND_PRODUCT))
        product.increaseWishCount()
        wishRepository.save(wish)
    }

    fun delete(command: DeleteWishCommand) {
        val wish = wishRepository.findByIdOrThrow(command.wishId, WishException(NOT_FOUND_WISH))
        wish.validateOwner(command.memberId)
        wishRepository.delete(wish)
        publisher.publishEvent(DecreaseWishCountEvent(wish.productId))
    }

    fun readAll(memberId: Long): List<WishResponse> {
        memberRepository.existByIdOrThrow(memberId, MemberException(NOT_FOUND_MEMBER))
        return wishRepository.readAllWishResponse(memberId)
    }
}
