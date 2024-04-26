package com.petqua.application.image

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UrlConverter(
    @Value("\${image.common.domain}")
    private val domain: String,
) {

    fun convertToAccessibleUrl(filePath: String, storedUrl: String): String {
        /*
        * example)
        * filePath = "root/directory/image.jpeg"
        * storedUrl = "https://storedUrl.com/root/directory/image.jpeg"
        *
        * pathIndex = 21
        * storedPath = "/root/directory/image.jpeg"
        * return "https://domain.com/root/directory/image.jpeg"
        * */
        val pathIndex = storedUrl.indexOf("/$filePath")
        val storedPath = storedUrl.substring(pathIndex)
        return "$domain$storedPath"
    }
}
