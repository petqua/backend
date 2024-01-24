package com.petqua.common.util

import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import jakarta.persistence.EntityManager

inline fun <reified T> EntityManager.createSingleQuery(
        query: SelectQuery<*>,
        context: JpqlRenderContext,
        renderer: JpqlRenderer,
): T? {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
            .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
            .singleResult
}

inline fun <reified T> EntityManager.createQuery(
        query: SelectQuery<*>,
        context: JpqlRenderContext,
        renderer: JpqlRenderer,
        limit: Int = 20,
): List<T> {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
            .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
            .setMaxResults(limit)
            .resultList
}

inline fun <reified T> EntityManager.createCountQuery(
        query: SelectQuery<*>,
        context: JpqlRenderContext,
        renderer: JpqlRenderer,
): Long {
    val rendered = renderer.render(query, context)
    return this.createQuery(rendered.query, T::class.java)
            .apply { rendered.params.forEach { (name, value) -> setParameter(name, value) } }
            .firstResult.toLong()
}
