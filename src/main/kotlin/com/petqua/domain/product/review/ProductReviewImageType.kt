package com.petqua.domain.product.review

import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.UNSUPPORTED_IMAGE_TYPE
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.util.Locale.ENGLISH

enum class ProductReviewImageType(
    val contentType: String,
) {

    IMAGE_JPEG(IMAGE_JPEG_VALUE),
    IMAGE_PNG(IMAGE_PNG_VALUE),
    ;

    companion object {
        fun validateSupported(contentType: String?) {
            contentType?.let {
                enumValues<ProductReviewImageType>().find {
                    it.contentType.uppercase() == contentType.uppercase(ENGLISH)
                }
            } ?: throw ProductReviewException(UNSUPPORTED_IMAGE_TYPE)
        }
    }
}
