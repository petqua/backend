package com.petqua.application.product.review

import com.petqua.application.image.ImageStorageService
import com.petqua.domain.image.ImageType
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.FAILED_REVIEW_IMAGE_UPLOAD
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Component
class ProductReviewImageUploader(
    private val imageStorageService: ImageStorageService,

    @Value("\${image.product-review.directory}")
    private val directory: String,
) {

    fun uploadAll(images: List<MultipartFile>): List<String> {
        if (images.isEmpty()) {
            return listOf()
        }
        return images.map { upload(it) }
    }

    private fun upload(image: MultipartFile): String {
        val type = ImageType.from(image.contentType)
        val fileName = UUID.randomUUID()
        val path = "$directory$fileName.${type.extension}"

        return uploadOrThrow(path, image)
    }

    private fun uploadOrThrow(path: String, image: MultipartFile): String {
        try {
            return imageStorageService.upload(path, image)
        } catch (e: Exception) {
            throw ProductReviewException(FAILED_REVIEW_IMAGE_UPLOAD)
        }
    }
}
