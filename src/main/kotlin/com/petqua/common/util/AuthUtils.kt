package com.petqua.common.util

import java.util.Base64

private const val EMPTY_PASSWORD = ""

object BasicAuthUtils {
    fun encodeCredentialsWithColon(
        userName: String,
        password: String = EMPTY_PASSWORD,
    ): String {
        val credentials = "$userName:$password"
        return Base64.getEncoder().encodeToString(credentials.toByteArray())
    }
}
