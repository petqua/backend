package com.petqua.application.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.domain.auth.LoginMemberOrGuest
import com.petqua.domain.keyword.ProductKeywordRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.product.WishProductRepository
import com.petqua.domain.product.detail.ProductImageRepository
import com.petqua.domain.product.dto.ProductResponse
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository,
    private val productKeywordRepository: ProductKeywordRepository,
    private val wishProductRepository: WishProductRepository,
) {

    @Transactional(readOnly = true)
    fun readById(loginMemberOrGuest: LoginMemberOrGuest, productId: Long): ProductDetailResponse {
        val productWithInfo = productRepository.findProductWithInfoByIdOrThrow(productId) {
            ProductException(NOT_FOUND_PRODUCT)
        }
        val imageUrls = productImageRepository.findProductImagesByProductId(productId).map { it.imageUrl }
        val isWished = loginMemberOrGuest.isMember() && wishProductRepository.existsByProductIdAndMemberId(
            productId = productId,
            memberId = loginMemberOrGuest.memberId
        )

        return ProductDetailResponse(productWithInfo, imageUrls, isWished)
    }

    @Transactional(readOnly = true)
    fun readAll(query: ProductReadQuery): ProductsResponse {
        val products = productRepository.findAllByCondition(query.toReadConditions(), query.toPaging())
        val markedProducts = markWishedProductOf(query.loginMemberOrGuest, products)
        val totalProductsCount = productRepository.countByReadCondition(query.toReadConditions())
        return ProductsResponse.of(markedProducts, query.limit, totalProductsCount)
    }

    private fun markWishedProductOf(
        loginMemberOrGuest: LoginMemberOrGuest,
        products: List<ProductResponse>
    ): List<ProductResponse> {
        if (loginMemberOrGuest.isMember()) {
            val wishedProductsIds = wishProductRepository.findWishedProductIdByMemberIdAndProductIdIn(
                memberId = loginMemberOrGuest.memberId,
                productIds = products.map { it.id }
            )
            return products.map { it.copy(isWished = wishedProductsIds.contains(it.id)) }
        }
        return products
    }

    @Transactional(readOnly = true)
    fun readBySearch(query: ProductSearchQuery): ProductsResponse {
        return if (productKeywordRepository.existsByWord(query.word)) {
            val products = productRepository.findByKeywordSearch(query.toCondition(), query.toPaging())
            val markedProducts = markWishedProductOf(query.loginMemberOrGuest, products)
            val totalProductsCount = productRepository.countByKeywordSearchCondition(query.toCondition())
            ProductsResponse.of(markedProducts, query.limit, totalProductsCount)
        } else {
            val products = productRepository.findBySearch(query.toCondition(), query.toPaging())
            val markedProducts = markWishedProductOf(query.loginMemberOrGuest, products)
            val totalProductsCount = productRepository.countBySearchCondition(query.toCondition())
            ProductsResponse.of(markedProducts, query.limit, totalProductsCount)
        }
    }

    @Transactional(readOnly = true)
    fun readAutoCompleteKeywords(query: ProductKeywordQuery): List<ProductKeywordResponse> {
        val productKeyword = query.toProductKeyword()
        return productKeywordRepository.findBySearch(productKeyword.word, query.limit)
    }
}
