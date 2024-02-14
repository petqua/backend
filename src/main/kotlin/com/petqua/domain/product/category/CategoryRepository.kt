package com.petqua.domain.product.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRepository : JpaRepository<Category, Long>, CategoryCustomRepository {

    @Query("select new com.petqua.domain.product.category.SpeciesResponse(c.species.name) from Category c where c.family= :family")
    fun findSpeciesByFamily(family: Family): List<SpeciesResponse>
}
