package com.petqua.domain.fish

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class FishCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : FishCustomRepository {

    override fun findBySpeciesName(speciesName: String, limit: Int): List<Fish> {
        val query = jpql {
            select(
                entity(Fish::class)
            ).from(
                entity(Fish::class)
            ).whereAnd(
                path(Fish::species)(Species::name).like(pattern = "%${speciesName}%")
            ).orderBy(
                locate(speciesName, path(Fish::species)(Species::name)).asc(),
                length(path(Fish::species)(Species::name)).asc(),
                path(Fish::species)(Species::name).asc()
            )
        }

        return entityManager.createQuery(
            query,
            jpqlRenderContext,
            jpqlRenderer,
            limit
        )
    }
}
