package com.petqua.domain.fish

import com.petqua.domain.fish.dto.SpeciesSearchResponse
import com.petqua.test.fixture.fish
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class FishCustomRepositoryImplTest(
    private val fishRepository: FishRepository,
) : BehaviorSpec({

    Given("검색을 통해 어종 목록을 조회할 때") {
        val fishA = fishRepository.save(fish(species = "베타"))
        val fishB = fishRepository.save(fish(species = "베일 테일"))
        val fishC = fishRepository.save(fish(species = "베일 임벨리스"))
        val fishD = fishRepository.save(fish(species = "임베리스"))

        When("어종 검색어를 입력하면") {
            val responses = fishRepository.findBySpeciesSearch(Species.from("베"), 5)

            Then("관련된 어종 목록을 조회할 수 있다") {
                responses shouldBe listOf(
                    SpeciesSearchResponse(fishA.species.name),
                    SpeciesSearchResponse(fishD.species.name),
                    SpeciesSearchResponse(fishB.species.name),
                    SpeciesSearchResponse(fishC.species.name),
                )
            }
        }

        When("어종 검색어와 개수를 입력하면") {
            val responses = fishRepository.findBySpeciesSearch(Species.from("베"), 1)

            Then("입력한 개수만큼 어종 목록을 조회할 수 있다") {
                responses shouldBe listOf(
                    SpeciesSearchResponse(fishA.species.name),
                )
            }
        }
    }
})
