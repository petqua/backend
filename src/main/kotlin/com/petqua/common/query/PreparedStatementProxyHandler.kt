package com.petqua.common.query

import jakarta.annotation.Nonnull
import jakarta.annotation.Nullable
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import kotlin.system.measureTimeMillis

private const val EXECUTE = "execute"

class PreparedStatementProxyHandler(
    private val queryInfo: QueryInfo,
) : MethodInterceptor {

    @Nullable
    override operator fun invoke(@Nonnull invocation: MethodInvocation): Any? {
        val method = invocation.method
        if (method.name.contains(EXECUTE)) {
            return measureTimeMillis {
                invocation.proceed()
            }.also {
                queryInfo.time += it
                queryInfo.increaseCount()
            }
        }
        return invocation.proceed()
    }
}
