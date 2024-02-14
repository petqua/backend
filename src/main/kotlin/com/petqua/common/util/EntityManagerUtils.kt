package com.petqua.common.util

import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import jakarta.persistence.EntityManager

inline fun <reified T> EntityManager.createSingleQueryOrThrow(
    query: SelectQuery<*>,
    context: JpqlRenderContext,
    renderer: JpqlRenderer,
    exceptionSupplier: () -> RuntimeException = { IllegalArgumentException("Query did not return any result") },
): T {
    val rendered = renderer.render(query, context)
    val results = this.createQuery(rendered.query, T::class.java)
        .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
        .resultList
    return if (results.isNotEmpty()) results.single() else throw exceptionSupplier()
}

inline fun <reified T> EntityManager.createQuery(
    query: SelectQuery<*>,
    context: JpqlRenderContext,
    renderer: JpqlRenderer,
): List<T> {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
        .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
        .resultList
}

inline fun <reified T> EntityManager.createQuery(
    query: SelectQuery<*>,
    context: JpqlRenderContext,
    renderer: JpqlRenderer,
    limit: Int,
): List<T> {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
        .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
        .setMaxResults(limit)
        .resultList
}

inline fun <reified T> EntityManager.exists(
    query: SelectQuery<*>,
    context: JpqlRenderContext,
    renderer: JpqlRenderer,
): Boolean {
    val rendered = renderer.render(query, context)
    val resultList = this.createQuery(rendered.query, T::class.java)
        .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
        .setMaxResults(1)
        .resultList
    return resultList.isNotEmpty()
}

inline fun <reified T> EntityManager.createCountQuery(
    query: SelectQuery<*>,
    context: JpqlRenderContext,
    renderer: JpqlRenderer,
): T {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
        .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
        .singleResult
}
