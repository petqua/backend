package com.petqua.application.fish

import com.petqua.application.fish.dto.SpeciesSearchQuery
import com.petqua.domain.fish.FishRepository
import com.petqua.domain.fish.dto.SpeciesSearchResponse
import com.petqua.exception.fish.FishException
import com.petqua.exception.fish.FishExceptionType.INVALID_SPECIES_NAME
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.fish
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class FishServiceTest(
    private val fishService: FishService,
    private val fishRepository: FishRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("검색어 자동완성을 통해 어종 목록을 조회할 때") {
        val fishA = fishRepository.save(fish(species = "베타"))
        val fishB = fishRepository.save(fish(species = "베일 테일"))
        val fishC = fishRepository.save(fish(species = "베일 임벨리스"))
        val fishD = fishRepository.save(fish(species = "임베리스"))

        When("어종 검색어를 입력하면") {
            val responses = fishService.readAutoCompleteSpecies(
                SpeciesSearchQuery(
                    word = "베"
                )
            )

            Then("관련된 어종 목록을 조회할 수 있다") {
                responses shouldBe listOf(
                    SpeciesSearchResponse(fishA.id, fishA.species.name),
                    SpeciesSearchResponse(fishD.id, fishD.species.name),
                    SpeciesSearchResponse(fishB.id, fishB.species.name),
                    SpeciesSearchResponse(fishC.id, fishC.species.name),
                )
            }
        }

        When("어종 검색어와 개수를 입력하면") {
            val responses = fishService.readAutoCompleteSpecies(
                SpeciesSearchQuery(
                    word = "베",
                    limit = 2
                )
            )

            Then("입력한 개수만큼 관련된 어종 목록을 조회할 수 있다") {
                responses shouldBe listOf(
                    SpeciesSearchResponse(fishA.id, fishA.species.name),
                    SpeciesSearchResponse(fishD.id, fishD.species.name),
                )
            }
        }

        When("어종 검색어를 입력하지 않으면") {

            Then("예외를 던진다") {
                shouldThrow<FishException> {
                    fishService.readAutoCompleteSpecies(
                        SpeciesSearchQuery(word = "")
                    )
                }.exceptionType() shouldBe INVALID_SPECIES_NAME
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
