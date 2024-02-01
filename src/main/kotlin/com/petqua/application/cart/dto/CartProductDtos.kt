package com.petqua.application.cart.dto

import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.cart.DeliveryMethod
import com.petqua.domain.product.dto.ProductResponse

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

    companion object {
        fun of(cartProduct: CartProduct, productResponse: ProductResponse): CartProductResponse {
            return CartProductResponse(
                id = cartProduct.id,
                storeName = productResponse.storeName,
                productId = productResponse.id,
                productName = productResponse.name,
                productThumbnailUrl = productResponse.thumbnailUrl,
                productPrice = productResponse.price,
                productDiscountRate = productResponse.discountRate,
                productDiscountPrice = productResponse.discountPrice,
                quantity = cartProduct.quantity.value,
                isMale = cartProduct.isMale,
                deliveryMethod = cartProduct.deliveryMethod.name,
                isOnSale = true,
            )
        }


        fun fromDeletedProduct(cartProduct: CartProduct): CartProductResponse {
            return CartProductResponse(
                id = cartProduct.id,
                storeName = "",
                productId = cartProduct.productId,
                productName = "",
                productThumbnailUrl = "",
                productPrice = 0,
                productDiscountRate = 0,
                productDiscountPrice = 0,
                quantity = cartProduct.quantity.value,
                isMale = cartProduct.isMale,
                deliveryMethod = cartProduct.deliveryMethod.name,
                isOnSale = false,
            )
        }
    }
}
