package com.petqua.application.cart.dto

import com.petqua.common.domain.Money
import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Product
import com.petqua.domain.product.option.Sex
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class SaveCartProductCommand(
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val sex: Sex,
    val deliveryMethod: DeliveryMethod,
    val deliveryFee: Money,
) {
    fun toCartProduct(): CartProduct {
        return CartProduct(
            memberId = memberId,
            productId = productId,
            quantity = CartProductQuantity(quantity),
            sex = sex,
            deliveryMethod = deliveryMethod,
            deliveryFee = deliveryFee,
        )
    }
}


data class UpdateCartProductOptionCommand(
    val memberId: Long,
    val cartProductId: Long,
    val quantity: CartProductQuantity,
    val sex: Sex,
    val deliveryMethod: DeliveryMethod,
    val deliveryFee: Money,
)

data class DeleteCartProductCommand(
    val memberId: Long,
    val cartProductId: Long,
)

data class CartProductWithSupportedOptionResponse(
    @Schema(
        description = "봉달(장바구니) 상품 id",
        example = "1"
    )
    val id: Long,

    @Schema(
        description = "상품 판매점",
        example = "S아쿠아"
    )
    val storeName: String,

    @Schema(
        description = "상품 id",
        example = "1"
    )
    val productId: Long,

    @Schema(
        description = "상품 이름",
        example = "알비노 풀레드 아시안 고정구피"
    )
    val productName: String,

    @Schema(
        description = "상품 썸네일 이미지",
        example = "https://docs.petqua.co.kr/products/thumbnails/thumbnail1.jpeg"
    )
    val productThumbnailUrl: String,

    @Schema(
        description = "상품 가격",
        example = "30000"
    )
    val productPrice: Money,

    @Schema(
        description = "가격 할인율",
        example = "30"
    )
    val productDiscountRate: Int,

    @Schema(
        description = "할인 가격(판매 가격)",
        example = "21000"
    )
    val productDiscountPrice: Money,

    @Schema(
        description = "봉달(장바구니) 수량",
        example = "1"
    )
    val quantity: Int,

    @Schema(
        description = "성별",
        example = "MALE",
        allowableValues = ["MALE", "FEMALE", "HERMAPHRODITE"]
    )
    val sex: Sex,

    @Schema(
        description = "배송 방법(\"COMMON : 일반\", \"SAFETY : 안전\", \"PICK_UP : 직접\")",
        example = "SAFETY",
        allowableValues = ["COMMON", "SAFETY", "PICK_UP"]
    )
    val deliveryMethod: String,

    @Schema(
        description = "배송비",
        example = "3000"
    )
    val deliveryFee: Money,

    @Schema(
        description = "판매 여부(품절 및 삭제 확인)",
        example = "true"
    )
    val isOnSale: Boolean,


    @Schema(
        description = "안전 배송 가격 (null인 경우 지원 X)",
        example = "5000"
    )
    val safeDeliveryFee: Money?,

    @Schema(
        description = "일반 배송 가격 (null인 경우 지원 X)",
        example = "3000"
    )
    val commonDeliveryFee: Money?,

    @Schema(
        description = "픽업 배송 가격 (null인 경우 지원 X)",
        example = "0"
    )
    val pickUpDeliveryFee: Money?,

    @Schema(
        description = "수컷 추가 가격 (null인 경우 지원 X)",
        example = "1000"
    )
    val maleAdditionalPrice: Money?,

    @Schema(
        description = "암컷 추가 가격 (null인 경우 지원 X)",
        example = "1000"
    )
    val femaleAdditionalPrice: Money?,
) {

    constructor(
        cartProductResponse: CartProductResponse,
        maleAdditionalPrice: Money?,
        femaleAdditionalPrice: Money?,
    ) : this(
        id = cartProductResponse.id,
        storeName = cartProductResponse.storeName,
        productId = cartProductResponse.productId,
        productName = cartProductResponse.productName,
        productThumbnailUrl = cartProductResponse.productThumbnailUrl,
        productPrice = cartProductResponse.productPrice,
        productDiscountRate = cartProductResponse.productDiscountRate,
        productDiscountPrice = cartProductResponse.productDiscountPrice,
        quantity = cartProductResponse.quantity,
        sex = cartProductResponse.sex,
        deliveryMethod = cartProductResponse.deliveryMethod,
        deliveryFee = cartProductResponse.deliveryFee,
        isOnSale = cartProductResponse.isOnSale,
        safeDeliveryFee = cartProductResponse.safeDeliveryFee,
        commonDeliveryFee = cartProductResponse.commonDeliveryFee,
        pickUpDeliveryFee = cartProductResponse.pickUpDeliveryFee,
        maleAdditionalPrice = maleAdditionalPrice,
        femaleAdditionalPrice = femaleAdditionalPrice,
    )
}

data class CartProductResponse(
    val id: Long,
    val storeName: String,
    val productId: Long,
    val productName: String,
    val productThumbnailUrl: String,
    val productPrice: Money,
    val productDiscountRate: Int,
    val productDiscountPrice: Money,
    val quantity: Int,
    val sex: Sex,
    val deliveryMethod: String,
    val deliveryFee: Money,
    val isOnSale: Boolean,
    val safeDeliveryFee: Money?,
    val commonDeliveryFee: Money?,
    val pickUpDeliveryFee: Money?,
) {

    constructor(
        cartProduct: CartProduct,
        product: Product?,
        storeName: String?,
    ) : this(
        id = cartProduct.id,
        storeName = storeName ?: "",
        productId = product?.id ?: 0L,
        productName = product?.name ?: "",
        productThumbnailUrl = product?.thumbnailUrl ?: "",
        productPrice = product?.price ?: Money.from(BigDecimal.ZERO),
        productDiscountRate = product?.discountRate ?: 0,
        productDiscountPrice = product?.discountPrice ?: Money.from(BigDecimal.ZERO),
        quantity = cartProduct.quantity.value,
        sex = cartProduct.sex,
        deliveryMethod = cartProduct.deliveryMethod.name,
        deliveryFee = cartProduct.deliveryFee,
        isOnSale = product != null,
        safeDeliveryFee = product?.safeDeliveryFee,
        commonDeliveryFee = product?.commonDeliveryFee,
        pickUpDeliveryFee = product?.pickUpDeliveryFee,
    )
}
