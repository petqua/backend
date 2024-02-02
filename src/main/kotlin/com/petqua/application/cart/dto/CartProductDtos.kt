package com.petqua.application.cart.dto

import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.cart.DeliveryMethod
import com.petqua.domain.product.Product

data class SaveCartProductCommand(
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val isMale: Boolean,
    val deliveryMethod: DeliveryMethod,
) {
    fun toCartProduct(): CartProduct {
        return CartProduct(
            memberId = memberId,
            productId = productId,
            quantity = CartProductQuantity(quantity),
            isMale = isMale,
            deliveryMethod = deliveryMethod,
        )
    }
}


data class UpdateCartProductOptionCommand(
    val memberId: Long,
    val cartProductId: Long,
    val quantity: CartProductQuantity,
    val isMale: Boolean,
    val deliveryMethod: DeliveryMethod,
)

data class DeleteCartProductCommand(
    val memberId: Long,
    val cartProductId: Long,
)

data class CartProductResponse(
    val id: Long,
    val storeName: String,
    val productId: Long,
    val productName: String,
    val productThumbnailUrl: String,
    val productPrice: Int,
    val productDiscountRate: Int,
    val productDiscountPrice: Int,
    val quantity: Int,
    val isMale: Boolean,
    val deliveryMethod: String,
    val isOnSale: Boolean,
) {

    constructor(cartProduct: CartProduct, product: Product?, storeName: String?) : this(
        cartProduct.id,
        storeName ?: "",
        product?.id ?: cartProduct.productId,
        product?.name ?: "",
        product?.thumbnailUrl ?: "",
        product?.price?.intValueExact() ?: 0,
        product?.discountRate ?: 0,
        product?.discountPrice?.intValueExact() ?: 0,
        cartProduct.quantity.value,
        cartProduct.isMale,
        cartProduct.deliveryMethod.name,
        product != null
    )
}
