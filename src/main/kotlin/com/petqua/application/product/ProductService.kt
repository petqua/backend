package com.petqua.application.product

import com.petqua.application.product.dto.ProductDetailResponse
import com.petqua.application.product.dto.ProductKeywordQuery
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.application.product.dto.ProductReadQuery
import com.petqua.application.product.dto.ProductSearchQuery
import com.petqua.application.product.dto.ProductsResponse
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.keyword.ProductKeywordRepository
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

    @Transactional(readOnly = true)
    fun readById(productId: Long): ProductDetailResponse {
        val product = productRepository.findByIdOrThrow(productId, ProductException(NOT_FOUND_PRODUCT))
        val store = storeRepository.findByIdOrThrow(product.storeId, StoreException(NOT_FOUND_STORE))

        return ProductDetailResponse(product, store.name, product.averageReviewScore())
    }

    @Transactional(readOnly = true)
    fun readAll(query: ProductReadQuery): ProductsResponse {
        val products = productRepository.findAllByCondition(query.toReadConditions(), query.toPaging())
        val totalProductsCount = productRepository.countByCondition(query.toReadConditions())

        return ProductsResponse.of(products, query.limit, totalProductsCount)
    }

    @Transactional(readOnly = true)
    fun readBySearch(query: ProductSearchQuery): ProductsResponse {
        return if (productKeywordRepository.existsByWord(query.word)) {
            val products = productRepository.findByKeywordSearch(query.toSearchCondition(), query.toPaging())
            val totalProductsCount = productRepository.countByKeywordCondition(query.toSearchCondition())
            ProductsResponse.of(products, query.limit, totalProductsCount)
        } else {
            val products = productRepository.findBySearch(query.toSearchCondition(), query.toPaging())
            val totalProductsCount = productRepository.countByCondition(query.toSearchCondition())
            ProductsResponse.of(products, query.limit, totalProductsCount)
        }
    }

    @Transactional(readOnly = true)
    fun readKeywords(query: ProductKeywordQuery): List<ProductKeywordResponse> {
        val productKeyword = query.toProductKeyword()
        return productKeywordRepository.findBySearch(productKeyword.word, query.limit)
    }
}
