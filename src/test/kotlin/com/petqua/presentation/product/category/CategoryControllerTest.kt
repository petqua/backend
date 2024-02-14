package com.petqua.presentation.product.category

import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.category
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.restassured.common.mapper.TypeRef
import org.springframework.http.HttpStatus

class CategoryControllerTest(
    private val categoryRepository: CategoryRepository,
) : ApiTestConfig() {

    init {

        Given("어종 카테고리를 조회할 때") {
            categoryRepository.save(
                category(
                    family = "송사리과",
                    species = "고정구피",
                )
            )

            When("family 조건을 입력하면") {
                val response = requestReadSpecies(family = "송사리과")

                Then("조건에 해당하는 species 목록이 반환된다") {
                    val speciesResponses = response.`as`(object : TypeRef<List<SpeciesResponse>>() {})

                    response.statusCode shouldBe HttpStatus.OK.value()
                    speciesResponses shouldContainExactly listOf(
                        SpeciesResponse("고정구피")
                    )
                }
            }
        }
    }
}
