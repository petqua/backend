package com.petqua.domain.product.review

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class ProductReviewCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : ProductReviewCustomRepository {
}
