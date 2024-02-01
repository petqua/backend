package com.petqua.application.product

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionalEventListener

@Async
@Component
class ProductEventListener(
    private val productService: ProductService
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    fun decreaseWishCount(event: DecreaseWishCountEvent) {
        productService.decreaseWishCount(event.productId)
    }
}
