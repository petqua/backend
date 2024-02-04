package com.petqua.domain.keyword

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.application.product.dto.ProductKeywordResponse
import com.petqua.common.util.createQuery
import com.petqua.common.util.exists
import jakarta.persistence.EntityManager

private const val ESCAPE_LETTER = '\\'

class ProductKeywordCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductKeywordCustomRepository {

    override fun findBySearch(word: String, limit: Int): List<ProductKeywordResponse> {
        val query = jpql {
            selectDistinctNew<ProductKeywordResponse>(
                path(ProductKeyword::word)
            ).from(
                entity(ProductKeyword::class)
            ).whereAnd(
                path(ProductKeyword::word).like(pattern = "%$word%", escape = ESCAPE_LETTER)
            ).orderBy(
                length(path(ProductKeyword::word)).asc(), path(ProductKeyword::word).asc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            limit
        )
    }

    override fun existsByWord(word: String): Boolean {
        val query = jpql {
            select(
                path(ProductKeyword::id)
            ).from(
                entity(ProductKeyword::class)
            ).whereAnd(
                path(ProductKeyword::word).like(pattern = word, escape = ESCAPE_LETTER)
            )
        }

        return entityManager.exists<ProductKeyword>(
            query,
            jpqlRenderContext,
            jpqlRenderer
        )
    }
}
