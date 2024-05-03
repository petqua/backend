package com.petqua.application.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageStorageService(
    private val amazonS3: AmazonS3,
    private val urlConverter: UrlConverter,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) {

    fun upload(path: String, image: MultipartFile): String {
        val metadata = ObjectMetadata()
        metadata.contentType = image.contentType
        metadata.contentLength = image.size

        amazonS3.putObject(bucket, path, image.inputStream, metadata)

        val storedUrl = amazonS3.getUrl(bucket, path).toString()
        return urlConverter.convertToAccessibleUrl(path, storedUrl)
    }
}
