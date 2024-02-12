package com.petqua.common.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

private const val REQUEST_ID = "requestId"

@Component
class MdcFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val requestId = UUID.randomUUID().toString()
        MDC.put(REQUEST_ID, requestId)
        request.setAttribute(REQUEST_ID, requestId)
        chain.doFilter(request, response)
        MDC.clear();
    }
}
