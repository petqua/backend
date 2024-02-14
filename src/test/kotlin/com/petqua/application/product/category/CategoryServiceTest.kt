package com.petqua.application.product.category

import com.petqua.domain.product.category.CategoryRepository
import com.petqua.domain.product.category.SpeciesResponse
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.category
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class CategoryServiceTest(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("카테고리를 조회할 때") {
        categoryRepository.save(
            category(
                family = "송사리과",
                species = "고정구피",
            )
        )

        val query = CategoryReadQuery(family = "송사리과")

        When("family 조건을 입력하면") {
            val speciesResponses = categoryService.readSpecies(query)

            Then("조건에 해당하는 species 목록이 반환된다") {
                speciesResponses shouldContainExactly listOf(
                    SpeciesResponse("고정구피")
                )
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
