package com.petqua.application.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordCommand
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadCommand
import com.petqua.application.product.dto.ProductSearchCommand
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.product.ProductKeywordRepository
import com.petqua.domain.product.ProductRepository
import com.petqua.domain.store.StoreRepository
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.NOT_FOUND_PRODUCT
import com.petqua.exception.store.StoreException
import com.petqua.exception.store.StoreExceptionType.NOT_FOUND_STORE
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository,
    private val productKeywordRepository: ProductKeywordRepository,
) {

    fun readById(productId: Long): ProductDetailResponse {
        val product = productRepository.findByIdOrThrow(productId, ProductException(NOT_FOUND_PRODUCT))
        val store = storeRepository.findByIdOrThrow(product.storeId, StoreException(NOT_FOUND_STORE))

        return ProductDetailResponse(product, store.name, product.averageReviewScore())
    }

    fun readAll(command: ProductReadCommand): ProductsResponse {
        val products = productRepository.findAllByCondition(command.toReadConditions(), command.toPaging())
        val totalProductsCount = productRepository.countByCondition(command.toReadConditions())

        return ProductsResponse.of(products, command.limit, totalProductsCount)
    }

    fun readBySearch(command: ProductSearchCommand): ProductsResponse {
        val products = productRepository.findBySearch(command.toSearchCondition(), command.toPaging())
        val totalProductsCount = productRepository.countByCondition(command.toSearchCondition())

        return ProductsResponse.of(products, command.limit, totalProductsCount)
    }

    fun readKeywords(command: ProductKeywordCommand): List<ProductKeywordResponse> {
        val productKeyword = command.toProductKeyword()
        return productKeywordRepository.findBySearch(productKeyword.word, command.limit)
    }
}
