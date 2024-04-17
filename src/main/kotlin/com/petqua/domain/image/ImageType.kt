package com.petqua.domain.image

import com.petqua.exception.image.ImageException
import com.petqua.exception.image.ImageExceptionType.INVALID_CONTENT_TYPE
import org.springframework.http.MediaType
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import java.util.Locale

enum class ImageType(
    val contentType: String,
    val extension: String,
) {

    IMAGE_JPEG(IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG.subtype),
    IMAGE_PNG(IMAGE_PNG_VALUE, MediaType.IMAGE_PNG.subtype),
    ;

    companion object {
        fun from(contentType: String?): ImageType {
            return contentType?.let {
                enumValues<ImageType>().find {
                    it.contentType.uppercase() == contentType.uppercase(
                        Locale.ENGLISH
                    )
                }
            }
                ?: throw ImageException(INVALID_CONTENT_TYPE)
        }
    }
}
