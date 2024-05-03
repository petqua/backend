package com.petqua.application.product.review

import com.petqua.application.image.ImageStorageService
import com.petqua.domain.product.review.ProductReviewImageType
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
        ProductReviewImageType.validateSupported(image.contentType)
        val fileName = UUID.randomUUID()
        val path = "$directory$fileName${parseFileExtension(image)}"

        return uploadOrThrow(path, image)
    }

    private fun parseFileExtension(image: MultipartFile): String {
        return image.originalFilename?.let {
            ".${it.substringAfterLast(FILE_EXTENSION_DELIMITER)}"
        } ?: EMPTY_EXTENSION
    }

    private fun uploadOrThrow(path: String, image: MultipartFile): String {
        try {
            return imageStorageService.upload(path, image)
        } catch (e: Exception) {
            throw ProductReviewException(FAILED_REVIEW_IMAGE_UPLOAD)
        }
    }

    companion object {
        private const val FILE_EXTENSION_DELIMITER = '.'
        private const val EMPTY_EXTENSION = ""
    }
}
