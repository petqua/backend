package com.petqua.common.query

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Aspect
@Component
class QueryCounterAop {

    private val log = LoggerFactory.getLogger(QueryCounterAop::class.java)
    private val queryInfoStorage = ThreadLocal<QueryInfo>()

    @Around("execution( * javax.sql.DataSource.getConnection())")
    fun getProxyConnection(joinPoint: ProceedingJoinPoint): Any {
        val connection = joinPoint.proceed()
        return ConnectionProxyHandler(connection, getQueryInfo()).getProxy()
    }

    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    fun logQueryInfo() {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val request = attributes?.request
        request?.let {
            val queryInfo = getQueryInfo()
            log.info("METHOD: ${request.method}, URI: ${request.requestURI}, $queryInfo")
        }
        queryInfoStorage.remove()
    }

    private fun getQueryInfo(): QueryInfo {
        if (queryInfoStorage.get() == null) {
            queryInfoStorage.set(QueryInfo())
        }
        return queryInfoStorage.get()
    }
}
