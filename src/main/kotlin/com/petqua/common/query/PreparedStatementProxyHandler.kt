package com.petqua.common.query

import jakarta.annotation.Nonnull
import jakarta.annotation.Nullable
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

private const val EXECUTE = "execute"

class PreparedStatementProxyHandler(
    private val queryInfo: QueryInfo,
) : MethodInterceptor {

    @Nullable
    override operator fun invoke(@Nonnull invocation: MethodInvocation): Any? {
        val method = invocation.method
        if (method.name.contains(EXECUTE)) {
            val startTime = System.currentTimeMillis()
            val result = invocation.proceed()
            val endTime = System.currentTimeMillis()
            queryInfo.time += endTime - startTime
            queryInfo.increaseCount()
            return result
        }
        return invocation.proceed()
    }
}
