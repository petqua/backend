package com.petqua.common.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.NativeWebRequest

fun NativeWebRequest.getHttpServletRequestOrThrow(
    exceptionSupplier: () -> Exception = { IllegalStateException("HTTP 요청에 접근할 수 없습니다") },
): HttpServletRequest {
    return this.getNativeRequest(HttpServletRequest::class.java) ?: throw exceptionSupplier()
}

fun HttpServletRequest.getCookieValueOrThrow(
    name: String,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("$name 쿠키를 찾을 수 없습니다") },
): String {
    val cookies = this.cookies ?: throw exceptionSupplier()
    return cookies.first() { it.name == name }.value ?: throw exceptionSupplier()
}

fun HttpServletRequest.getHeaderOrThrow(
    name: String,
    exceptionSupplier: () -> Exception = { IllegalArgumentException("$name 헤더를 찾을 수 없습니다") },
): String {
    return this.getHeader(name) ?: throw exceptionSupplier()
}
