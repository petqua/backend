package com.petqua.application

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.ProductRepository
import com.petqua.domain.StoreRepository
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
) {

    fun readById(productId: Long): ProductDetailResponse {
        val product = productRepository.findByIdOrThrow(productId, ProductException(NOT_FOUND_PRODUCT))
        val store = storeRepository.findByIdOrThrow(product.storeId, StoreException(NOT_FOUND_STORE))

        return ProductDetailResponse(product, store.name, product.averageReviewScore())
    }
}
