package com.petqua.domain.fish

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.petqua.common.util.createQuery
import com.petqua.domain.fish.dto.SpeciesSearchResponse
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class FishCustomRepositoryImpl(
    private val entityManager: EntityManager,
    private val jpqlRenderContext: JpqlRenderContext,
    private val jpqlRenderer: JpqlRenderer,
) : FishCustomRepository {

    override fun findBySpeciesSearch(species: Species, limit: Int): List<SpeciesSearchResponse> {
        val query = jpql {
            selectNew<SpeciesSearchResponse>(
                path(Fish::species)(Species::name)
            ).from(
                entity(Fish::class)
            ).whereAnd(
                path(Fish::species)(Species::name).like(pattern = "%${species.name}%")
            ).orderBy(
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
